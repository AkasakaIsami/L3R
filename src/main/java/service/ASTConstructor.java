package service;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.TypeDeclaration;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import com.github.javaparser.ast.stmt.Statement;
import entity.MyStatement;

import entity.node.StatementNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.FileUtil;

import java.util.Optional;

/**
 * @author Akasaka Isami
 * @description 针对单个文件的AST构造器，同时提供一些静态方法
 * @date 2022-12-12 22:00:12
 */
public class ASTConstructor {

    private static final Logger logger = LoggerFactory.getLogger(FileUtil.class);

    /**
     * 输入一个MethodDeclaration AST节点，遍历其statement，并为每个statement创建一个AST
     *
     * @param node
     */
    public static StatementNode buildMethodAST(Node node, String fileName) {
        if (!(node instanceof MethodDeclaration)) {
            logger.warn("ASTConstructor: 输入的节点不是MethodDeclaration");
            return null;
        }

        MethodDeclaration methodDeclaration = ((MethodDeclaration) node).asMethodDeclaration();

        if (methodDeclaration.getParentNode().isPresent() && !(node.getParentNode().get() instanceof TypeDeclaration)) {
            logger.warn("ASTConstructor: 匿名类方法不处理");
            return null;
        }

        String method = methodDeclaration.getDeclarationAsString(true, false, true);
        logger.info("ASTConstructor: 正在处理的方法" + method);

        // 创建方法声明语句的实体和节点
        MyStatement mdStatment = new MyStatement(methodDeclaration, fileName);
        StatementNode mdStatmentNode = new StatementNode(mdStatment);


        // 获得方法体内所有的语句后遍历
        StatementNode tempNode = mdStatmentNode;

        Optional<BlockStmt> body = methodDeclaration.getBody();
        if (body.isPresent()) {
            NodeList<Statement> statements = body.get().getStatements();

            for (Statement statement : statements) {
                tempNode = dfs(mdStatmentNode, tempNode, statement);

            }
        }

        return null;
    }

    private static StatementNode dfs(StatementNode root, StatementNode parentNode, Statement currentStatement) {





        return null;
    }


}