package analysisTools;

import soot.jimple.AbstractStmtSwitch;
import soot.jimple.AssignStmt;
import soot.jimple.BreakpointStmt;
import soot.jimple.EnterMonitorStmt;
import soot.jimple.ExitMonitorStmt;
import soot.jimple.GotoStmt;
import soot.jimple.IdentityStmt;
import soot.jimple.IfStmt;
import soot.jimple.InvokeStmt;
import soot.jimple.LookupSwitchStmt;
import soot.jimple.NopStmt;
import soot.jimple.RetStmt;
import soot.jimple.ReturnStmt;
import soot.jimple.ReturnVoidStmt;
import soot.jimple.TableSwitchStmt;
import soot.jimple.ThrowStmt;
import staticFamily.StaticStmt;


class JimpleStmtSolver extends AbstractStmtSwitch{
    Object result;
    StaticStmt s;

    public JimpleStmtSolver(StaticStmt s) {
    	this.s = s;
    }
    /**

     * it would never appear in jimple
     */
    public void caseBreakpointStmt(BreakpointStmt stmt)
    {
       s.setIsBreakpointStmt(true);
    }

    /**
     * There are 3 types of invoke stmt:
     * 1. staticinvoke: invoking static methods
     * 2. virtualinvoke: invoking non-static methods base on class of the object
     * 3. specialinvoke: invoking init, private methods, and inherited methods
     * */
    public void caseInvokeStmt(InvokeStmt stmt)
    {
        s.setIsInvokeStmt(true);
    }

    /**
     * Some facts:
     *  invokeStmt can only be rightOp;
     *  fieldRef can be leftOp or RightOp.
     * */
    public void caseAssignStmt(AssignStmt stmt)
    {
        s.setIsAssignStmt(true);
    }
    /**
     * There are only 3 known types of identity stmt:
     * 1. localizing 'this'
     * 2. localizing parameters
     * 3. r1 = @caughtexception
     * Format: leftOp := @1/2/3: type
     * e.g.: $r0 := @this: myjava.awt.datatransfer.DataFlavor
     * */
    public void caseIdentityStmt(IdentityStmt stmt)
    {
        s.setIsIdentityStmt(true);
    }

    /**
     * these 2 actually exist
     * */
    public void caseEnterMonitorStmt(EnterMonitorStmt stmt)
    {
        s.setIsEnterMonitorStmt(true);
    }
    public void caseExitMonitorStmt(ExitMonitorStmt stmt)
    {
        s.setIsExitMonitorStmt(true);
    }

    /**
     * In the output jimple file, labels were added
     * but the labels are not included in stmt.
     * e.g., stmt "if $i0==0 goto staticinvoke<a: a>()"
     * will look like:
     * "if $i0==0 goto label1"
     * and the actual code would be:
     * "label1:
     * 		staticinvoke<a: a>()"
     * */
    public void caseGotoStmt(GotoStmt stmt)
    {
        s.setIsGotoStmt(true);
    }

    public void caseIfStmt(IfStmt stmt)
    {
    	s.setIsIfStmt(true);
    }

    public void caseLookupSwitchStmt(LookupSwitchStmt stmt)
    {
        s.setIsLookupSwitchStmt(true);
    }

    public void caseNopStmt(NopStmt stmt)
    {
        s.setIsNopStmt(true);
    }

    public void caseRetStmt(RetStmt stmt)
    {
        s.setIsRetStmt(true);
    }

    public void caseReturnStmt(ReturnStmt stmt)
    {
        s.setIsReturnStmt(true);
    }

    public void caseReturnVoidStmt(ReturnVoidStmt stmt)
    {
        s.setIsReturnVoidStmt(true);
    }

    public void caseTableSwitchStmt(TableSwitchStmt stmt)
    {
        s.setIsTableSwitchStmt(true);
    }

    public void caseThrowStmt(ThrowStmt stmt)
    {
        s.setIsThrowStmt(true);
    }

    public void defaultCase(Object obj)
    {
    }

    public void setResult(Object result)
    {
        this.result = result;
    }

    public Object getResult()
    {
        return result;
    }
}