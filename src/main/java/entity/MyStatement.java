package entity;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.MethodDeclaration;

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
     * 项目名_文件名_起始行数
     */
    private String id;


    public MyStatement(Node node, String fileName) {

        if (node instanceof MethodDeclaration) {
            this.originalRootNode = ((MethodDeclaration) node).asMethodDeclaration();
            this.fileName = fileName;

            this.type = StatementType.METHOD_DECLARATION;
            this.rowNum = originalRootNode.getBegin().isPresent() ? originalRootNode.getBegin().get().line : -1;
            this.content = ((MethodDeclaration) originalRootNode).getDeclarationAsString(true, true, true);

            this.id = fileName + "@" + rowNum;
        }


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
}
