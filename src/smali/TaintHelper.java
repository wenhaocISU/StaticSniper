package smali;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import staticFamily.StaticApp;
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
	
	public void setTargetLine(int targetLine) {
		this.targetLine = targetLine;
	}
	
	public void TargetLineAnalysis() {
		StaticSmaliStmt tgtStmt = m.getSmaliStmtByLineNumber(targetLine);
		if (tgtStmt == null) {
			System.out.println("Error: can not find the line number " + targetLine);
			return;
		}
		System.out.println(tgtStmt.getSourceLineNumber() + " " + tgtStmt.getSmaliStmt());
		List<StaticSmaliStmt> sourceStmts = findJumpSourceStmt(tgtStmt);
		boolean allReachedEnd = false;
		int i = 1;
		while (!allReachedEnd) {
			System.out.println("\n\n" + i + " run: ");
			List<StaticSmaliStmt> newSrcStmts = new ArrayList<StaticSmaliStmt>();
			for (StaticSmaliStmt srcStmt : sourceStmts) {
				if (blockAlreadyHit(srcStmt.getBlockLabel())) {
					System.out.println("already hit: " + srcStmt.getSourceLineNumber() + "  " + srcStmt.getSmaliStmt());
					if (!newSrcStmts.contains(srcStmt)) {
						newSrcStmts.add(srcStmt);
						System.out.println("-added- " + srcStmt.getSourceLineNumber() + "  " + srcStmt.getSmaliStmt());
					}
				} else {
					System.out.println("didn't hit: " + srcStmt.getSourceLineNumber() + "  " + srcStmt.getSmaliStmt());
					List<StaticSmaliStmt> toAddNewSrcStmts = findJumpSourceStmt(srcStmt);
					System.out.println("  " + toAddNewSrcStmts.size() + " more found.");
					for (StaticSmaliStmt toAdd : toAddNewSrcStmts)
						if (!newSrcStmts.contains(toAdd)) {
							newSrcStmts.add(toAdd);
							System.out.println("-added- " + srcStmt.getSourceLineNumber() + "  " + srcStmt.getSmaliStmt());
						}
				}
			}
			allReachedEnd = true;
			for (StaticSmaliStmt newSrc : newSrcStmts) {
				if (!blockAlreadyHit(newSrc.getBlockLabel())) {
					allReachedEnd = false;
					break;
				}
				else {
					System.out.println("-HIT- " + newSrc.getSourceLineNumber() + " " + newSrc.getBlockLabel().toString());
				}
			}
			sourceStmts = newSrcStmts;
		}
		// now we have a list of stmts that were hit, but did not jump to our targets
		System.out.println("--- final ---");
		for (StaticSmaliStmt src : sourceStmts) {
			System.out.println(src.getSourceLineNumber() + "  " + src.getSmaliStmt());
			
		}
	}
	
	
	private boolean blockAlreadyHit(BlockLabel bl) {
		for (BlockLabel hitBL : blocksHit) {
			if (hitBL.isSameNormalLabel(bl))
				return true;
		}
		return false;
	}
	
 	public List<StaticSmaliStmt> findJumpSourceStmt(StaticSmaliStmt tgtStmt) {
 		System.out.println("   looking for src for stmt " + tgtStmt.getSourceLineNumber() + " " + tgtStmt.getSmaliStmt());
		StaticSmaliStmt firstStmtInBlock = getFirstStmtInBlock(tgtStmt.getBlockLabel());
		System.out.println("   moving to first stmt in block " + firstStmtInBlock.getSourceLineNumber() + " " + firstStmtInBlock.getSmaliStmt());
		List<StaticSmaliStmt> result = new ArrayList<StaticSmaliStmt>();
		for (StaticSmaliStmt srcStmt : m.getSmaliStatements()) {
			if (srcStmt.getBlockLabel().isSameNormalLabel(tgtStmt.getBlockLabel()))
				continue;
			// 1. neighbor (normal stmts or if stmts can be a source)
			if (srcStmt.getStmtID() == firstStmtInBlock.getStmtID()-1) {
				System.out.println("   checking up1 stmt: " + srcStmt.getSourceLineNumber());
				if (srcStmt.flowsThrough() || srcStmt.branches()) {
					System.out.println("   up1 stmt fits.");
					result.add(srcStmt);
				}
			}
			// 2. goto
			else if (srcStmt.goesTo()) {
				System.out.println("   checking goto stmt " + srcStmt.getSourceLineNumber());
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
				System.out.println("   checking switch stmt " + srcStmt.getSourceLineNumber() + " " + srcStmt.getSmaliStmt());
				String switchTableLabel = srcStmt.getSwitchTableLabel();
				System.out.println("    switch table label is at " + switchTableLabel);
				for (StaticSmaliStmt switchTableStmt : m.getSmaliStatements()) {
					// find the switch table content
					if (!switchTableStmt.isSwitchTable() || !switchTableStmt.getSwitchTableName().equals(switchTableLabel))
						continue;

					Map<String, String> switchTable = switchTableStmt.getSwitchTable();
					System.out.println("    found switch table " + switchTable.size());
					boolean useful = false;
					// if any entry in the table points to tgtS's label, srcS is a source.
					for (Map.Entry<String, String> entry : switchTable.entrySet()) {
						System.out.println("     found an target label " + entry.getValue());
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
		System.out.println("   found " + result.size() + " src");
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
