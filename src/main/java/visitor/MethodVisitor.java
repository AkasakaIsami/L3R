package visitor;

import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.TypeDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import model.GraphNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import service.*;
import utils.FileUtil;
import view.CFG_ASTPrinter;
import view.CFG_DFGPrinter;

import java.util.List;
import java.util.UUID;

/**
 * @author Akasaka Isami
 * @description 单个文件的函数遍历器 遍历函数的同时为每个statement构造AST
 * @date 2022-12-13 21:22:30
 */
public class MethodVisitor extends VoidVisitorAdapter<String> {
    private static final Logger logger = LoggerFactory.getLogger(MethodVisitor.class);

    @Override
    public void visit(MethodDeclaration node, String fileName) {

        if (node.getType() == null || !node.getParentNode().isPresent()) {
            logger.warn("");
            return;
        }

        String methodName = node.getDeclarationAsString(true, false, true);
        logger.info("MethodVisitor: 正在处理的方法为" + methodName);

        if (!(node.getParentNode().get() instanceof TypeDeclaration)) {
            logger.warn("MethodVisitor: 匿名对象的方法不处理");
            return;
        }

        CFGCreater cfgCreater = new CFGCreater();
        List<GraphNode> graphNodes = cfgCreater.buildMethodCFG(node);

        ASTCreater astCreater = new ASTCreater(cfgCreater.getAllNodesMap());
        astCreater.buildMethodAST(node);

        DFGCreater dfgCreater = new DFGCreater(cfgCreater.getAllNodesMap());
        dfgCreater.buildMethodDFG(node);

        String path = "/Users/akasakaisami/Study/Grade3/L3R/result/zookeeper/" + FileUtil.extractFileName(fileName) + "/" + node.getNameAsString();

        for (GraphNode graphNode : graphNodes) {
            CFG_ASTPrinter printer2 = new CFG_ASTPrinter(path, dfgCreater.getAllDFGEdgesList());
            printer2.print(graphNode, node.getNameAsString(), node.getParameters().size() + "_" + UUID.randomUUID().toString().substring(0, 6), false);

        }


    }
}