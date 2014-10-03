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
    
    public void caseBreakpointStmt(BreakpointStmt stmt)
    {
       s.setIsBreakpointStmt(true);
    }

    public void caseInvokeStmt(InvokeStmt stmt)
    {
        s.setIsInvokeStmt(true);
    }

    public void caseAssignStmt(AssignStmt stmt)
    {
        s.setIsAssignStmt(true);
    }

    public void caseIdentityStmt(IdentityStmt stmt)
    {
        s.setIsIdentityStmt(true);
    }

    public void caseEnterMonitorStmt(EnterMonitorStmt stmt)
    {
        s.setIsEnterMonitorStmt(true);
    }

    public void caseExitMonitorStmt(ExitMonitorStmt stmt)
    {
        s.setIsExitMonitorStmt(true);
    }

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