package service;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.TypeDeclaration;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import com.github.javaparser.ast.stmt.IfStmt;
import com.github.javaparser.ast.stmt.Statement;
import entity.MyStatement;
import entity.StatementType;
import entity.node.StatementNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;


/**
 * @author Akasaka Isami
 * @description
 * @date 2022-12-12 22:07:26
 */
public class CFGConstructor {

    private static final Logger logger = LoggerFactory.getLogger(CFGConstructor.class);

    /**
     * 输入一个方法 构造方法内所有的语句并构建该方法的CFG 返回方法CFG的根节点
     * 以Statement根node为起点，前序dfs按序遍历所有语句
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
                tempNode = dfs(mdStatmentNode, tempNode, statement);

            }
        }


        return null;

    }

    /**
     * dfs遍历函数的每个statement，为每个statement创建StatementNode并构建cfg的边
     *
     * @param root             函数声明的根节点
     * @param parentNode       当前statement的前一个statement
     * @param currentStatement 当前正在处理的statement
     * @return 当前创建完的StatementNode
     */
    private static StatementNode dfs(StatementNode root, StatementNode parentNode, Statement currentStatement) {


        if (currentStatement instanceof ExpressionStmt) {
            /**
             * 递归终止条件 1：输入的是一个expression语句
             * 顺序连接父节点，直接返回即可
             *
             * 需要注意的是, LambdaExpr是一种corner case
             */
            MyStatement myStatement = new MyStatement(currentStatement, "");
            StatementNode curNode = new StatementNode(myStatement);

            connectNodes(parentNode, curNode);

            return curNode;

        } else if (currentStatement instanceof IfStmt) {
            /**
             * 条件控制语句1 if语句
             * 对于所有条件的代码块，将每个分支的子节点都连接于同一个placeholder
             */

            IfStmt tempIfStmt = ((IfStmt) currentStatement).asIfStmt();

            MyStatement placeholder = new MyStatement("", StatementType.BLANK, -1, "#placeholder");
            StatementNode placeholderNode = new StatementNode(placeholder);


            // 遍历所有分支
            while (tempIfStmt != null) {

                // if与前面的statement相连
                MyStatement ifStatement = new MyStatement(tempIfStmt, "");
                StatementNode ifNode = new StatementNode(ifStatement);
                connectNodes(parentNode, ifNode);

                // 遍历分支块内的语句
                StatementNode tempNode = ifNode;
                if (tempIfStmt.getThenStmt().isBlockStmt()) {
                    BlockStmt thenBlockStmt = tempIfStmt.getThenStmt().asBlockStmt();
                    NodeList<Statement> statements = thenBlockStmt.getStatements();
                    for (Statement statement : statements) {
                        tempNode = dfs(root, tempNode, statement);
                    }
                } else {
                    tempNode = dfs(root, tempNode, tempIfStmt.getThenStmt());
                }

                connectNodes(tempNode, placeholderNode);

                // 对后续else块进行处理
                if (tempIfStmt.getElseStmt().isPresent()) {
                    if (tempIfStmt.getElseStmt().get().isIfStmt()) {
                        // else if 的话直接继续循环 其父节点要设置为if语句
                        tempIfStmt = tempIfStmt.getElseStmt().get().asIfStmt();
                        parentNode = ifNode;
                        continue;
                    } else {
                        // else的话

//                        if(!tempIfStmt.getElseStmt().get().isBlockStmt()){
//                            tempNode = dfs(tempNode, elseNode, tempIfStmt.getElseStmt().get());
//                        }else {
//                            BlockStmt elseBlockStmt = tempIfStmt.getElseStmt().get().asBlockStmt();
//                            NodeList<Statement> statements1 = elseBlockStmt.getStatements();
//                            tempNode = elseNode;
//                            for (Statement statement : statements1) {
//                                tempNode = dfs(tempNode, elseNode, statement);
//                            }
//                        }





                    }



                } else {
                    // 不存在else或else if的话 直接结束分支
//                    currentStatement()
                    tempIfStmt = null;

                }


            }


        }


        return null;

    }

    /**
     * CFG上连接两个statement node
     *
     * @param parentNode 父节点
     * @param curNode    子节点
     */
    private static void connectNodes(StatementNode parentNode, StatementNode curNode) {
        parentNode.addCFGChild(curNode);
        curNode.addCFGParent(parentNode);
    }


}
