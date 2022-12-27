package view;

import model.AstNode;
import model.EdgeTypes;
import model.GraphEdge;
import model.GraphNode;
import utils.DotPrintFilter;
import utils.LogUtil;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.*;


public class CFG_ASTPrinter {

    private String path;
    private StringBuilder str;
    private int index = 0;

    private int index2 = 0;
    private List<String> leafNodes;

    // key: 语句的节点编号  value：语句的ast dot字符串
    // 遍历这个map，写入语句ast文件
    private Map<String, String> ASTStrMap;


    public CFG_ASTPrinter(String path) {
        this.path = path;
        str = new StringBuilder("digraph {");
        leafNodes = new ArrayList<>();
        ASTStrMap = new HashMap<>();
    }

    private void BFS(GraphNode root, boolean ncs) {
        Queue<GraphNode> dealingNodes = new LinkedList<>();
        dealingNodes.add(root);
        while (!dealingNodes.isEmpty()) {

            // par是一条语句
            GraphNode par = dealingNodes.poll();
            String parIndexNum = "";
            if (par.getDotNum() != null) {
                parIndexNum = par.getDotNum();
            } else {
                parIndexNum = "n" + (index++);
                par.setDotNum(parIndexNum);
                str.append(System.lineSeparator() + par.getDotNum() + " [label=\"" + DotPrintFilter.filterQuotation(par.getOriginalCodeStr()) + "\" , line=" + par.getCodeLineNum() + ", hasException=\" " + par.getIsExceptionLabel() + "\"];");


                // 我改的 创建AST的代码
                String label = DotPrintFilter.filterQuotation(par.getOriginalCodeStr());
                String line = par.getCodeLineNum() + "";
                String dotnum = par.getDotNum();
                String key = dotnum + '_' + label + '_' + line;
                StringBuilder value = new StringBuilder();
                value.append("digraph {");

                dfs(par.getAstRootNode(), "", value);

                value.append(System.lineSeparator()).append("}");
                index2 = 0;
                ASTStrMap.put(key, value.toString());

//                ASTRecurive(par.getAstRootNode(), par.getDotNum());
            }

            List<GraphNode> adjacentPoints = par.getAdjacentPoints();
            for (GraphNode child : adjacentPoints) {
                if (child.getDotNum() == null) {

                    dealingNodes.add(child);
                    child.setDotNum("n" + (index));
                    index++;
                    str.append(System.lineSeparator() + child.getDotNum() + " [label=\"" + DotPrintFilter.filterQuotation(child.getOriginalCodeStr()) + "\" , line=" + child.getCodeLineNum() + ", hasException=\" " + child.getIsExceptionLabel() + "\"];");

                    // 我改的 创建AST的代码
                    String label = DotPrintFilter.filterQuotation(child.getOriginalCodeStr());
                    String line = child.getCodeLineNum() + "";
                    String dotnum = child.getDotNum();
                    String key = dotnum + '_' + label + '_' + line;
                    StringBuilder value = new StringBuilder();
                    value.append("digraph {");

                    dfs(child.getAstRootNode(), "", value);

                    value.append(System.lineSeparator()).append("}");
                    index2 = 0;
                    ASTStrMap.put(key, value.toString());

//                    ASTRecurive(child.getAstRootNode(), child.getDotNum());
                }
            }
//            for (GraphEdge edge : par.getEdgs()) {
//                str.append(System.lineSeparator() + edge.getOriginalNode().getDotNum() + " -> " + edge.getAimNode().getDotNum() + "[color=" + edge.getType().getColor() + "];");
//
//            }


            for (GraphEdge edge : par.getEdgs()) {
                if (LogUtil.isLogStatement(edge.getAimNode().getOriginalCodeStr())) {
                    // 如果当前节点的这条边指向的是log语句 直接跳过
                    continue;
                } else if (LogUtil.isLogStatement(edge.getOriginalNode().getOriginalCodeStr())) {
                    // 如果当前节点就是log语句
                    // 连接log语句的父节点和log语句的子节点

                    List<GraphNode> parents = edge.getOriginalNode().getPreAdjacentPoints();
                    List<GraphNode> children = edge.getOriginalNode().getAdjacentPoints();

                    int size = children.size();
                    if (size == 0) continue;

                    GraphNode child = children.get(0);

                    for (GraphNode parent :
                            parents) {
                        str.append(System.lineSeparator()).append(parent.getDotNum()).append(" -> ").append(child.getDotNum()).append("[color=").append(edge.getType().getColor()).append("];");

                    }
//                    str.append(System.lineSeparator() + edge.getOriginalNode().getDotNum() + " -> " + edge.getAimNode().getDotNum() + "[color=" + edge.getType().getColor() + "];");

                } else {
                    // 其他所有正常情况
                    str.append(System.lineSeparator()).append(edge.getOriginalNode().getDotNum()).append(" -> ").append(edge.getAimNode().getDotNum()).append("[color=").append(edge.getType().getColor()).append("];");
                }

            }


        }
        if (ncs) {
            NCS(leafNodes);
        }
        str.append(System.lineSeparator() + "}");
    }

    private void dfs(AstNode node, String parentNodeName, StringBuilder str) {
        if (node != null) {
            List<String> attributes = node.getAttributes();
            List<AstNode> subNodes = node.getSubNodes();
            List<String> subLists = node.getSubLists();
            List<List<AstNode>> subListNodes = node.getSubListNodes();

            String ndName = "n" + (index2++);

            if (!node.toString().equals("")) {
                str.append(System.lineSeparator()).append(ndName).append(" [label=\"").append(DotPrintFilter.AstNodeFilter(node.getTypeName())).append("\", ast_node=\"true\"];");
            }

            if (!parentNodeName.isEmpty()) {
                str.append(System.lineSeparator()).append(parentNodeName).append(" -> ").append(ndName).append("[color=").append(EdgeTypes.AST.getColor()).append("];");
            }

            for (String a : attributes) {
                String attrName = "n" + (index2++);
                str.append(System.lineSeparator()).append(attrName).append(" [label=\"").append(DotPrintFilter.AstNodeFilter(a)).append("\", ast_node=\"true\"];");
                str.append(System.lineSeparator()).append(ndName).append(" -> ").append(attrName).append("[color=").append(EdgeTypes.AST.getColor()).append("];");
            }

            for (AstNode subNode : subNodes) {
                dfs(subNode, ndName, str);
            }

            for (int i = 0; i < subLists.size(); i++) {
                String ndLstName = "n" + (index2++);
                str.append(System.lineSeparator()).append(ndLstName).append(" [label=\"").append(DotPrintFilter.AstNodeFilter(subLists.get(i))).append("\", ast_node=\"true\"];");
                str.append(System.lineSeparator()).append(ndName).append(" -> ").append(ndLstName).append("[color=").append(EdgeTypes.AST.getColor()).append("];");

                for (int j = 0; j < subListNodes.get(i).size(); j++) {
                    dfs(subListNodes.get(i).get(j), ndLstName, str);
//                    ASTRecurive(subListNodes.get(i).get(j), ndLstName);
                }

            }


        }
    }

    private void ASTRecurive(AstNode node, String parentNodeName) {
        if (node != null) {
            List<String> attributes = node.getAttributes();
            List<AstNode> subNodes = node.getSubNodes();
            List<String> subLists = node.getSubLists();
            List<List<AstNode>> subListNodes = node.getSubListNodes();
            String ndName = nextNodeName();
            if (!node.toString().equals("")) {
                str.append(System.lineSeparator() + ndName + " [label=\"" + DotPrintFilter.AstNodeFilter(node.getTypeName()) + "\", ast_node=\"true\"];");
            }
            if (parentNodeName != null) {
                str.append(System.lineSeparator() + parentNodeName + " -> " + ndName + "[color=" + EdgeTypes.AST.getColor() + "];");
            }
            for (String a : attributes) {
                String attrName = nextNodeName();
                str.append(System.lineSeparator() + attrName + " [label=\"" + DotPrintFilter.AstNodeFilter(a) + "\", ast_node=\"true\"];");
                str.append(System.lineSeparator() + ndName + " -> " + attrName + "[color=" + EdgeTypes.AST.getColor() + "];");
                if (DotPrintFilter.AstNodeFilter(a).contains("identifier") || DotPrintFilter.AstNodeFilter(a).contains("value")) {
                    leafNodes.add(attrName);
                }
            }
            for (int i = 0; i < subNodes.size(); i++) {
                ASTRecurive(subNodes.get(i), ndName);
            }
            for (int i = 0; i < subLists.size(); i++) {
                String ndLstName = nextNodeName();
                str.append(System.lineSeparator() + ndLstName + " [label=\"" + DotPrintFilter.AstNodeFilter(subLists.get(i)) + "\", ast_node=\"true\"];");
                str.append(System.lineSeparator() + ndName + " -> " + ndLstName + "[color=" + EdgeTypes.AST.getColor() + "];");
                for (int j = 0; j < subListNodes.get(i).size(); j++) {
                    ASTRecurive(subListNodes.get(i).get(j), ndLstName);
                }
            }
        }
    }

    private String nextNodeName() {
        return "n" + (index++);
    }

    public void NCS(List<String> allLeafNodes) {
        if (allLeafNodes.size() > 1) {
            for (int i = 1; i < allLeafNodes.size(); i++) {
                str.append(System.lineSeparator() + allLeafNodes.get(i - 1) + " -> " + allLeafNodes.get(i) + "[color=" + EdgeTypes.NCS.getColor() + "];");
            }
        }
    }

    public void print(GraphNode root, String methodName, String methodParms, boolean ncs) {
        BFS(root, ncs);
        File file = new File(path + "/statements");
        if (!file.exists()) {
            file.mkdirs();
        }
        try {
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(
                    new File(path + "/" + methodName + "." + methodParms + "_CA.dot")));
            bufferedWriter.write(str.toString());
            bufferedWriter.flush();
            bufferedWriter.close();
        } catch (Exception e) {
            System.out.println("数据写入ast文件发送异常！");
        }


        // 我写的
        for (Map.Entry<String, String> entry : ASTStrMap.entrySet()) {
            String filename = entry.getKey();
            String filecontent = entry.getValue();
            try {
                BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(
                        new File(path + "/statements/" + filename + ".dot")));
                bufferedWriter.write(filecontent);
                bufferedWriter.flush();
                bufferedWriter.close();

            } catch (Exception e) {
                System.out.println("数据写入ast文件发送异常！");
            }

        }


    }
}
