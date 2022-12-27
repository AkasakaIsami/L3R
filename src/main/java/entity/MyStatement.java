package entity;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.stmt.ExpressionStmt;

/**
 * @author Akasaka Isami
 * @description
 * @date 2022-12-12 22:12:15
 */
public class MyStatement {

    /**
     * 通过java parser解析后获得的Statement根节点
     */
    private Node originalRootNode;

    /**
     * 语句的所属的文件名
     */
    private String fileName;


    /**
     * 语句的类型
     */
    private StatementType type;

    /**
     * 语句所在的起始行数
     */
    private int rowNum;

    /**
     * 语句的原始内容
     */
    private String content;


    /**
     * 项目名_文件名@起始行数
     */
    private String id;


    public MyStatement(Node node, String fileName) {

        if (node instanceof MethodDeclaration) {
            this.type = StatementType.METHOD_DECLARATION;
            this.rowNum = node.getBegin().isPresent() ? node.getBegin().get().line : -1;
            this.content = ((MethodDeclaration) node).getDeclarationAsString(true, true, true);

        } else if (node instanceof ExpressionStmt) {
            this.type = StatementType.EXPRESSION;
            this.rowNum = node.getBegin().isPresent() ? node.getBegin().get().line : -1;
            this.content = ((ExpressionStmt) node).getExpression().toString();
        }

        this.originalRootNode = node;
        this.fileName = fileName;
        this.id = fileName + "@" + rowNum;

    }

    public MyStatement(String fileName, StatementType type, int rowNum, String content) {
        this.fileName = fileName;
        this.type = type;
        this.rowNum = rowNum;
        this.content = content;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public int getRowNum() {
        return rowNum;
    }

    public void setRowNum(int rowNum) {
        this.rowNum = rowNum;
    }

    public Node getOriginalRootNode() {
        return originalRootNode;
    }

    public void setOriginalRootNode(Node originalRootNode) {
        this.originalRootNode = originalRootNode;
    }

    public StatementType getType() {
        return type;
    }

    public void setType(StatementType type) {
        this.type = type;
    }
}
