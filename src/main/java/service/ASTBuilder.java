package service;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.FileUtil;

import java.io.File;
import java.io.FileNotFoundException;

/**
 * @author Akasaka Isami
 * @description 针对单个文件的AST构造器，同时提供一些静态方法
 * @date 2022-12-12 22:00:12
 */
public class ASTBuilder {

    private static final Logger logger = LoggerFactory.getLogger(FileUtil.class);

    private final CompilationUnit cu;

    ASTBuilder(String filePath) throws FileNotFoundException {
        cu = JavaParser.parse(new File(filePath));
    }

    public CompilationUnit getCompilationUnit() {
        return cu;
    }


    /**
     * 输入一个MethodDeclaration AST节点，遍历其statement，并为每个statement创建一个AST
     * @param node
     */
    public void buildMethodAST(Node node) {

    }


    public void buildStatementAST(Node node) {

        if (node instanceof ExpressionStmt) {
            logger.info("buildStatementAST: 正在构建Expression Statement的AST");

            ExpressionStmt exStmt = ((ExpressionStmt) node).asExpressionStmt();
            Expression expression = exStmt.getExpression();

            String label = expression.toString();

//            int lineNum = expression.getBegin().isPresent() ? expression.getBegin().get().line : -1;
//            GraphNode cfgNode = allCFGNodesMap.get(label + ":" + lineNum);
//            if(cfgNode==null){
//                System.out.println("stop");
//            }
//            AstNode astNode = new AstNode();
//            AstNodeInit astNodeInit = new AstNodeInit(true, astNode);
//            astNodeInit.Init(expression);
//            cfgNode.setAstRootNode(astNode);
//        }

        }


    }

    public static void main(String[] args) throws FileNotFoundException {
        ASTBuilder builder = new ASTBuilder("C:\\Users\\akasa\\Study\\毕业设计\\L3R\\src\\main\\java\\ReversePolishNotation.java");
        System.out.println(builder.cu.getClass());

    }


}