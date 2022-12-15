package entity.node;

import entity.MyStatement;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Akasaka Isami
 * @description
 * @date 2022-12-14 16:37:09
 */
public class StatementNode {

    private MyStatement statement;

    private List<StatementNode> CFGChildren;
    private List<StatementNode> DFGChildren;

    private List<StatementNode> CFGParents;
    private List<StatementNode> DFGParents;

    public StatementNode(MyStatement statement) {
        this.statement = statement;

        CFGChildren = new ArrayList<>();
        DFGChildren = new ArrayList<>();

        CFGParents = new ArrayList<>();
        DFGParents = new ArrayList<>();
    }

    public MyStatement getStatement() {
        return statement;
    }

    public void setStatement(MyStatement statement) {
        this.statement = statement;
    }

    public List<StatementNode> getCFGChildren() {
        return CFGChildren;
    }

    public void setCFGChildren(List<StatementNode> CFGChildren) {
        this.CFGChildren = CFGChildren;
    }

    public List<StatementNode> getDFGChildren() {
        return DFGChildren;
    }

    public void setDFGChildren(List<StatementNode> DFGChildren) {
        this.DFGChildren = DFGChildren;
    }

    public List<StatementNode> getCFGParents() {
        return CFGParents;
    }

    public void setCFGParents(List<StatementNode> CFGParents) {
        this.CFGParents = CFGParents;
    }

    public List<StatementNode> getDFGParents() {
        return DFGParents;
    }

    public void setDFGParents(List<StatementNode> DFGParents) {
        this.DFGParents = DFGParents;
    }
}
