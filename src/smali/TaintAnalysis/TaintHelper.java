package smali.TaintAnalysis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import smali.BlockLabel;
import staticFamily.StaticApp;
import staticFamily.StaticClass;
import staticFamily.StaticField;
import staticFamily.StaticMethod;
import staticFamily.StaticSmaliStmt;

public class TaintHelper {

	private StaticApp testApp;
	
	private StaticMethod m;
	private Map<BlockLabel, List<StaticSmaliStmt>> blockGraph;
	private List<BlockLabel> blocksHit;
	
	private ArrayList<Integer> linesHit;
	
	private int targetLine;

	
	public TaintHelper(StaticApp staticApp) {
		this.testApp = staticApp;
		this.m = null;
		this.linesHit = new ArrayList<Integer>();
		this.blockGraph = new HashMap<BlockLabel, List<StaticSmaliStmt>>();
		this.blocksHit = new ArrayList<BlockLabel>();
	}
	
	public void setMethod(StaticMethod m) {
		this.m = m;
		this.blockGraph = m.getBlockGraph();
	}

	public ArrayList<Integer> getBPsHit() {
		return linesHit;
	}
	
	public void setBPsHit(ArrayList<Integer> linesHit) {
		
		this.linesHit = linesHit;
		
		for (int l : linesHit) {
			StaticSmaliStmt stmtHit = m.getSmaliStmtByLineNumber(l);
			boolean labelExists = false;
			for (BlockLabel bl : blocksHit) {
				if (bl.isSameNormalLabel(stmtHit.getBlockLabel())) {
					labelExists = true;
					break;
				}
			}
			if (!labelExists)
				blocksHit.add(stmtHit.getBlockLabel());
		}
		
	}
	
	
	public ArrayList<String> findTaintedMethods(int tgtLine) {
		this.targetLine = tgtLine;
		StaticSmaliStmt tgtStmt = m.getSmaliStmtByLineNumber(targetLine);
		List<StaticSmaliStmt> sourceStmts = findJumpSourceStmt(tgtStmt);
		if (tgtStmt == null) {
			System.out.println("Error: can not find the line number " + targetLine);
			return new ArrayList<String>();
		}
		//System.out.println("--- src ---\n" + tgtStmt.getSourceLineNumber() + " " + tgtStmt.getSmaliStmt());

		boolean allReachedEnd = false;
		//int i = 1;
		while (!allReachedEnd) {
			//System.out.println("\n" + i + " run: ");
			List<StaticSmaliStmt> newSrcStmts = new ArrayList<StaticSmaliStmt>();
			for (StaticSmaliStmt srcStmt : sourceStmts) {
				if (blockAlreadyHit(srcStmt.getBlockLabel())) {
					//System.out.println("already hit: " + srcStmt.getSourceLineNumber() + "  " + srcStmt.getSmaliStmt());
					if (!newSrcStmts.contains(srcStmt)) {
						newSrcStmts.add(srcStmt);
						//System.out.println("-added- " + srcStmt.getSourceLineNumber() + "  " + srcStmt.getSmaliStmt());
					}
				} else {
					//System.out.println("didn't hit: " + srcStmt.getSourceLineNumber() + "  " + srcStmt.getSmaliStmt());
					List<StaticSmaliStmt> toAddNewSrcStmts = findJumpSourceStmt(srcStmt);
					//System.out.println("  " + toAddNewSrcStmts.size() + " more found.");
					for (StaticSmaliStmt toAdd : toAddNewSrcStmts)
						if (!newSrcStmts.contains(toAdd)) {
							newSrcStmts.add(toAdd);
							//System.out.println("-added- " + srcStmt.getSourceLineNumber() + "  " + srcStmt.getSmaliStmt());
						}
				}
			}
			allReachedEnd = true;
			for (StaticSmaliStmt newSrc : newSrcStmts) {
				if (!blockAlreadyHit(newSrc.getBlockLabel())) {
					allReachedEnd = false;
					break;
				}
			}
			sourceStmts = newSrcStmts;
		}
		// now we have a list of stmts that were hit, but did not jump to our targets
		//System.out.println("\n--- final ---");
		//System.out.println("-Target- " + tgtStmt.getSourceLineNumber());
		//System.out.println("\n# responsible stmts for line " + tgtStmt.getSourceLineNumber());
		ArrayList<String> result = new ArrayList<String>();
		for (StaticSmaliStmt src : sourceStmts) {
			//System.out.println("  - " + src.getSourceLineNumber() + "  " + src.getSmaliStmt());
			ArrayList<String> stmtResult = ConditionStmtSolver(src);
			for (String sR : stmtResult)
				if (!result.contains(sR))
					result.add(sR);
		}
		return result;
	}
	
	private ArrayList<String> traceVariable(String tgtV, int lastLine) {
		String v = tgtV;
		ArrayList<String> result = new ArrayList<String>();
		if (v.equals("") || v.startsWith("p"))
			return result;
		//System.out.println("   * tracing variable " + tgtV + " from line " + lastLine);
		boolean useful = false;
		for (int i = linesHit.size()-1; i >= 0 ; i--) {
			if (linesHit.get(i) == lastLine) {
				useful = true;
				continue;
			}
			if (!useful)
				continue;
			StaticSmaliStmt s = m.getSmaliStmtByLineNumber(linesHit.get(i));
			  // is moved from another object
			if (!s.isMoveResultStmt() && s.getSmaliStmt().startsWith("move") && s.getSmaliStmt().contains(", ")) {
				String left = s.getSmaliStmt().split(", ")[0];
				String right = s.getSmaliStmt().split(", ")[1];
				if (left.endsWith(v)) {
					return traceVariable(right, s.getSourceLineNumber());
				}
			} // is from a method return
			else if (s.isMoveResultStmt() && s.getSmaliStmt().endsWith(" " + v)) {
				StaticSmaliStmt invokeS = m.getSmaliStmtByID(s.getMoveInvokeResultFromID());
				
				//System.out.println("   " + v + " in return of " + invokeS.getSourceLineNumber());
				// when this happens, the method parameters becomes our new target variable
				String parameters = invokeS.getSmaliStmt().substring(
						invokeS.getSmaliStmt().indexOf("{")+1,
						invokeS.getSmaliStmt().indexOf("}"));
				if (!parameters.contains(", ")) {
					result = traceVariable(parameters, invokeS.getSourceLineNumber());
				} else {
					for (String p : parameters.split(", ")) {
						ArrayList<String> subResult = traceVariable(p, invokeS.getSourceLineNumber());
						for (String sR : subResult)
							if (!result.contains(sR))
								result.add(sR);
					}
				}
				if (result.size() < 1) {
					result.add(invokeS.getInvokeTarget());
				}
				return result;
			} // is from a field
			else if (s.isGetFieldStmt()) {
				String firstV = s.getSmaliStmt().substring(
						s.getSmaliStmt().indexOf(" ")+1,
						s.getSmaliStmt().indexOf(", "));
				if (v.equals(firstV)) {
					String fieldRef = s.getSmaliStmt().substring(
							s.getSmaliStmt().lastIndexOf(", ")+2);
					String tgtClassName = fieldRef.split("->")[0].replace(";", "");
					String tgtFieldName = fieldRef.split("->")[1];
					tgtFieldName = tgtFieldName.split(":")[0];
					StaticClass c = testApp.findClassByDexName(tgtClassName);
					StaticField f = c.findFieldByName(tgtFieldName);
					//System.out.println(" #result for " + v + ": " + f.getDeclaration());
					return f.getInCallSourceSigs();
				}
			} // is a const
			else if (s.getSmaliStmt().startsWith("const") &&
					 s.getSmaliStmt().contains(" " + v + ", ")) {
				//System.out.println(" #result for " + v + ": it's a const");
					return result;
			} // is other operations
			else if (s.flowsThrough() &&
					s.getSmaliStmt().contains(" " + v + ", ")) {
				String right = s.getSmaliStmt().substring(
						s.getSmaliStmt().indexOf(" " + v + ", ") + (" " + v + ", ").length());
				//System.out.println(" #result for " + v + ": " + right);
				if (right.contains(", ")) {
					if (right.split(", ")[0].equals(v) && right.split(", ")[1].equals(v))
						continue;
					ArrayList<String> one = traceVariable(right.split(", ")[0], s.getSourceLineNumber());
					for (String r : one)
						if (!result.contains(r))
							result.add(r);
					ArrayList<String> two = traceVariable(right.split(", ")[1], s.getSourceLineNumber());
					for (String r : two)
						if (!result.contains(r))
							result.add(r);
					return result;
				} else {
					return traceVariable(right, s.getSourceLineNumber());
				}
			}
		}
		return result;
	}
	
	private ArrayList<String> ConditionStmtSolver(StaticSmaliStmt theStmt) {
		//System.out.println("solving " + theStmt.getSourceLineNumber() + " " + theStmt.getSmaliStmt());
		if (!theStmt.branches() && !theStmt.switches()){
			System.out.println("Solving Condition Stmt variable, unexpected situation...");
			System.out.println(theStmt.getSourceLineNumber() + " " + theStmt.getSmaliStmt());
			return new ArrayList<String>();
		}
		SmaliStmtParser ssP = new SmaliStmtParser(theStmt);
		Map<String, Boolean> vMap = new HashMap<String, Boolean>();
		vMap.put(ssP.getFirstVariable(), false);
		vMap.put(ssP.getSecondVariable(), false);
		///////// use the hit line
		ArrayList<String> one = traceVariable(ssP.getFirstVariable(), theStmt.getSourceLineNumber());
		ArrayList<String> two = traceVariable(ssP.getSecondVariable(), theStmt.getSourceLineNumber());
		for (String t : two)
			if (!one.contains(t))
				one.add(t);
		return one;
	}
	
	private boolean blockAlreadyHit(BlockLabel bl) {
		for (BlockLabel hitBL : blocksHit) {
			if (hitBL.isSameNormalLabel(bl))
				return true;
		}
		return false;
	}
	
 	private List<StaticSmaliStmt> findJumpSourceStmt(StaticSmaliStmt tgtStmt) {
 		//System.out.println("   looking for src for stmt " + tgtStmt.getSourceLineNumber() + " " + tgtStmt.getSmaliStmt());
		StaticSmaliStmt firstStmtInBlock = getFirstStmtInBlock(tgtStmt.getBlockLabel());
		//System.out.println("   moving to first stmt in block " + firstStmtInBlock.getSourceLineNumber() + " " + firstStmtInBlock.getSmaliStmt());
		List<StaticSmaliStmt> result = new ArrayList<StaticSmaliStmt>();
		for (StaticSmaliStmt srcStmt : m.getSmaliStatements()) {
			if (srcStmt.getBlockLabel().isSameNormalLabel(tgtStmt.getBlockLabel()))
				continue;
			// 1. neighbor (normal stmts or if stmts can be a source)
			if (srcStmt.getStmtID() == firstStmtInBlock.getStmtID()-1) {
				//System.out.println("   checking up1 stmt: " + srcStmt.getSourceLineNumber());
				if (srcStmt.flowsThrough() || srcStmt.branches()) {
					//System.out.println("   up1 stmt fits." + srcStmt.getSourceLineNumber());
					result.add(srcStmt);
				}
			}
			// 2. goto
			else if (srcStmt.goesTo()) {
				//System.out.println("   checking goto stmt " + srcStmt.getSourceLineNumber());
				String tgtLabel = srcStmt.getGotoTargetLabel();
				if (firstStmtInBlock.getBlockLabel().getNormalLabels().contains(tgtLabel))
					result.add(srcStmt);
			}
			// 3. if
			else if (srcStmt.branches()) {
				String tgtLabel = srcStmt.getIfTargetLabel();
				if (firstStmtInBlock.getBlockLabel().getNormalLabels().contains(tgtLabel))
					result.add(srcStmt);
			}
			// 4. switch
			else if (srcStmt.switches()) {
				//System.out.println("   checking switch stmt " + srcStmt.getSourceLineNumber() + " " + srcStmt.getSmaliStmt());
				String switchTableLabel = srcStmt.getSwitchTableLabel();
				//System.out.println("    switch table label is at " + switchTableLabel);
				for (StaticSmaliStmt switchTableStmt : m.getSmaliStatements()) {
					// find the switch table content
					if (!switchTableStmt.isSwitchTable() || !switchTableStmt.getSwitchTableName().equals(switchTableLabel))
						continue;

					Map<String, String> switchTable = switchTableStmt.getSwitchTable();
					//System.out.println("    found switch table " + switchTable.size());
					boolean useful = false;
					// if any entry in the table points to tgtS's label, srcS is a source.
					for (Map.Entry<String, String> entry : switchTable.entrySet()) {
						//System.out.println("     found an target label " + entry.getValue());
						if (firstStmtInBlock.getBlockLabel().getNormalLabels().contains(entry.getValue())) {
							useful = true;
							break;
						}
					}
					if (useful)
						result.add(srcStmt);
				}
			}
		}
		//System.out.println("   found " + result.size() + " src");
		return result;
	}
	
	private StaticSmaliStmt getFirstStmtInBlock(BlockLabel bl) {
		for (Map.Entry<BlockLabel, List<StaticSmaliStmt>> entry : blockGraph.entrySet()) {
			if (entry.getKey().isSameNormalLabel(bl)) {
				return entry.getValue().get(0);
			}
		}
		return null;
	}
	
	public void BPsHitAnalysis() {
		if (m == null) {
			System.out.println("Set the method first..");
			return;
		}
		System.out.println("=== JDB Log Analysis for method " + m.getFullJimpleSignature() + "\n");
		Map<BlockLabel, List<StaticSmaliStmt>> blockGraph = m.getBlockGraph();
		for (Map.Entry<BlockLabel, List<StaticSmaliStmt>> entry : blockGraph.entrySet()) {
			BlockLabel label = entry.getKey();
			List<StaticSmaliStmt> stmts = entry.getValue();
			System.out.println("-block- " + label.toString() + "\n");
			for (StaticSmaliStmt s : stmts) {
				System.out.println(s.getStmtID() + "\t" + s.getSmaliStmt());
				System.out.print(" *line=" + s.getSourceLineNumber());
				String hitOrNot = "not hit";
				if (linesHit.contains(s.getSourceLineNumber()))
					hitOrNot = "hit";
				System.out.print("  " + hitOrNot + "\n\n");
			}
		}
	}
	

	
}
