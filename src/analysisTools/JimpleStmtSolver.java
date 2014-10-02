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
       s.setContainsBreakpointStmt(true);
    }

    public void caseInvokeStmt(InvokeStmt stmt)
    {
        s.setContainsMethodCall(true);
    }

    public void caseAssignStmt(AssignStmt stmt)
    {
        s.setContainsAssignStmt(true);
    }

    public void caseIdentityStmt(IdentityStmt stmt)
    {
        s.setContainsIdentityStmt(true);
    }

    public void caseEnterMonitorStmt(EnterMonitorStmt stmt)
    {
        s.setContainsEnterMonitorStmt(true);
    }

    public void caseExitMonitorStmt(ExitMonitorStmt stmt)
    {
        s.setContainsExitMonitorStmt(true);
    }

    public void caseGotoStmt(GotoStmt stmt)
    {
        s.setContainsGotoStmt(true);
    }

    public void caseIfStmt(IfStmt stmt)
    {
    	s.setContainsIfStmt(true);
    }

    public void caseLookupSwitchStmt(LookupSwitchStmt stmt)
    {
        s.setContainsLookupSwitchStmt(true);
    }

    public void caseNopStmt(NopStmt stmt)
    {
        s.setContainsNopStmt(true);
    }

    public void caseRetStmt(RetStmt stmt)
    {
        s.setContainsRetStmt(true);
    }

    public void caseReturnStmt(ReturnStmt stmt)
    {
        s.setContainsReturnStmt(true);
    }

    public void caseReturnVoidStmt(ReturnVoidStmt stmt)
    {
        s.setContainsReturnVoidStmt(true);
    }

    public void caseTableSwitchStmt(TableSwitchStmt stmt)
    {
        s.setContainsTableSwitchStmt(true);
    }

    public void caseThrowStmt(ThrowStmt stmt)
    {
        s.setContainsThrowStmt(true);
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