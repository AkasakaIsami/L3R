package service;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.TypeDeclaration;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.Statement;
import entity.MyStatement;
import entity.node.StatementNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;


/**
 * @author Akasaka Isami
 * @description
 * @date 2022-12-12 22:07:26
 */
public class CFGBuilder {

    private static final Logger logger = LoggerFactory.getLogger(CFGBuilder.class);


    /**
     * 输入一个方法 构造方法内所有的语句并构建该方法的CFG 返回方法CFG的根节点
     *
     * @param node     待处理的方法的 javaparser AST 节点
     * @param fileName 方法所在的java文件的文件名
     * @return 构建完成后的该方法的CFG的根节点
     */
    public static StatementNode buildMethodCFG(Node node, String fileName) {

        if (!(node instanceof MethodDeclaration)) {
            logger.warn("CFGBuilder: 输入的节点不是MethodDeclaration");
            return null;
        }

        MethodDeclaration methodDeclaration = ((MethodDeclaration) node).asMethodDeclaration();


        if (methodDeclaration.getParentNode().isPresent() && !(node.getParentNode().get() instanceof TypeDeclaration)) {
            logger.warn("CFGBuilder: 匿名类方法不处理");
            return null;
        }

        String method = methodDeclaration.getDeclarationAsString(true, false, true);
        logger.info("CFGBuilder: 正在处理的方法" + method);

        // 创建方法声明语句的实体和节点
        MyStatement mdStatment = new MyStatement(methodDeclaration, fileName);
        StatementNode mdStatmentNode = new StatementNode(mdStatment);


        // 获得方法体内所有的语句后遍历
        StatementNode tempNode = mdStatmentNode;

        Optional<BlockStmt> body = methodDeclaration.getBody();
        if (body.isPresent()) {
            NodeList<Statement> statements = body.get().getStatements();

            for (Statement statement : statements) {
                tempNode = buildCFG(mdStatmentNode, tempNode, statement);
            }
        }

    }

    private static StatementNode buildCFG(StatementNode mdStatmentNode, StatementNode tempNode, Statement statement) {
    }


}
