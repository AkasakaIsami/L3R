package view;

import config.MConfig;
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
    private List<String> sentences;
    private int index = 0;

    private int index2 = 0;
    private List<String> leafNodes;

    // key: 语句的节点编号  value：语句的ast dot字符串
    // 遍历这个map，写入语句ast文件
    private Map<String, String> ASTStrMap;

    private Set<GraphEdge> allDFGEdgesList;


    public CFG_ASTPrinter(String path, Set<GraphEdge> allDFGEdgesList) {
        this.path = path;
        str = new StringBuilder("digraph {");
        leafNodes = new ArrayList<>();
        ASTStrMap = new HashMap<>();
        this.allDFGEdgesList = allDFGEdgesList;
        this.sentences = new ArrayList<>();
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
                // par.getDotNum() != null 说明当前节点还没被画过
                // 首先要判断自己是不是日志语句……
                // 应该没有人会啥b到连打两条日志语句吧…
                if (!LogUtil.isLogStatement(par.getOriginalCodeStr(), 1)) {
                    // 自己不是日志语句才会执行下面的代码
                    // 也就是把自己加入到cfg中 再把生成自己的ast
                    parIndexNum = "n" + (index++);
                    par.setDotNum(parIndexNum);
                    boolean isLogged = false;
                    List<GraphNode> adjacentPoints = par.getAdjacentPoints();
                    for (GraphNode child : adjacentPoints) {
                        if (LogUtil.isLogStatement(child.getOriginalCodeStr(), 1)) {
                            isLogged = true;
                            break;
                        }
                    }

                    str.append(System.lineSeparator()).append(par.getDotNum()).append(" [label=\"").append(DotPrintFilter.filterQuotation(par.getOriginalCodeStr())).append("\" , line=").append(par.getCodeLineNum()).append(", isLogged=\"").append(isLogged).append("\"];");


                    // 我改的 创建AST的代码
                    String label = DotPrintFilter.filterQuotation(par.getOriginalCodeStr());
                    String line = par.getCodeLineNum() + "";
                    String dotnum = par.getDotNum();
                    String key = dotnum + '_' + line;

                    StringBuilder value = new StringBuilder();
                    value.append("digraph {");

                    StringBuilder sentence = new StringBuilder();

                    // 这里分两种情况 第一种情况是这个node有ast树 那就递归写入ast文件就好了
                    // 第二种情况是 这个node没有ast树 那就创建一个单节点的树 把当前节点的值写进去就行了
                    // 一般没有ast的都是简单节点 比如"return" "case"之类的

                    if (par.getAstRootNode() == null) {
                        String node_value = par.getOpTypeStr();
                        String ndName = "n" + index2;
                        value.append(System.lineSeparator()).append(ndName).append(" [label=\"").append(node_value).append("\", ast_node=\"true\"];");

                        sentence.append(DotPrintFilter.cut(node_value));
                        sentences.add(sentence.toString());
                    } else {
                        dfs(par.getAstRootNode(), "", value, sentence);

                        // 去掉语料库最后的空格
                        if (sentence.length() != 0) sentence.deleteCharAt(sentence.length() - 1);
                        sentences.add(sentence.toString());
                    }


                    value.append(System.lineSeparator()).append("}");
                    index2 = 0;
                    ASTStrMap.put(key, value.toString());
                }
            }


            List<GraphNode> adjacentPoints = par.getAdjacentPoints();
            for (GraphNode child : adjacentPoints) {

                // 同样的 对于要创建边的子节点
                // 只有非日志语句的节点才会被创建节点
                // 但节点还是会被加入队列啦
                if (child.getDotNum() == null) {

                    dealingNodes.add(child);


                    if (!LogUtil.isLogStatement(child.getOriginalCodeStr(), 1)) {
                        child.setDotNum("n" + (index));
                        index++;


                        boolean isLogged = false;
                        List<GraphNode> grandChildren = child.getAdjacentPoints();
                        for (GraphNode grandchild : grandChildren) {
                            if (LogUtil.isLogStatement(grandchild.getOriginalCodeStr(), 1)) {
                                isLogged = true;
                                break;
                            }
                        }

                        str.append(System.lineSeparator() + child.getDotNum() + " [label=\"" + DotPrintFilter.filterQuotation(child.getOriginalCodeStr()) + "\" , line=" + child.getCodeLineNum() + ", isLogged=\" " + isLogged + "\"];");

                        // 我改的 创建AST的代码
                        String label = DotPrintFilter.filterQuotation(child.getOriginalCodeStr());
                        String line = child.getCodeLineNum() + "";
                        String dotnum = child.getDotNum();
                        String key = dotnum + '_' + line;
                        StringBuilder value = new StringBuilder();
                        value.append("digraph {");

                        StringBuilder sentence = new StringBuilder();
                        // 这里分两种情况 第一种情况是这个node有ast树 那就递归写入ast文件就好了
                        // 第二种情况是 这个node没有ast树 那就创建一个单节点的树 把当前节点的值写进去就行了
                        // 一般没有ast的都是简单节点 比如"return" "break" "finally" "else"之类的

                        if (child.getAstRootNode() == null) {
                            String node_value = child.getOpTypeStr();
                            String ndName = "n" + index2;
                            value.append(System.lineSeparator()).append(ndName).append(" [label=\"").append(node_value).append("\", ast_node=\"true\"];");

                            sentence.append(DotPrintFilter.cut(node_value));
                            sentences.add(sentence.toString());
                        } else {
                            dfs(child.getAstRootNode(), "", value, sentence);

                            // 去掉语料库最后的空格
                            if (sentence.length() != 0) sentence.deleteCharAt(sentence.length() - 1);
                            sentences.add(sentence.toString());
                        }


                        value.append(System.lineSeparator()).append("}");
                        index2 = 0;
                        ASTStrMap.put(key, value.toString());
                    }

                }
            }


            // 因为队列里还有log节点
            for (GraphEdge edge : par.getEdgs()) {
                if (LogUtil.isLogStatement(edge.getAimNode().getOriginalCodeStr(), 1)) {
                    // 如果当前节点的这条边指向的是log语句 直接跳过
                    continue;
                } else if (LogUtil.isLogStatement(edge.getOriginalNode().getOriginalCodeStr(), 1)) {
                    // 如果当前节点就是log语句
                    List<GraphNode> children = edge.getOriginalNode().getAdjacentPoints();
                    int size = children.size();
                    if (size == 0) continue;

                    GraphNode child = children.get(0);

                    // 如果子节点还是日志就别管了
                    // 向上回溯找到第一个不是日志语句的祖先节点
                    // 连接log语句的父节点和log语句的子节点
                    if (!LogUtil.isLogStatement(child.getOriginalCodeStr(), 1)) {

                        GraphNode ancestor = findNotLogAncestor(edge.getOriginalNode());

                        // 如果父亲节点的子节点里已经有我们当前的子节点了 那就跳过
                        List<GraphNode> brothers = ancestor.getAdjacentPoints();
                        if (brothers.contains(child))
                            continue;
                        str.append(System.lineSeparator()).append(ancestor.getDotNum()).append(" -> ").append(child.getDotNum()).append("[color=").append(edge.getType().getColor()).append("];");
                    }


                } else {
                    // 其他所有正常情况
                    str.append(System.lineSeparator()).append(edge.getOriginalNode().getDotNum()).append(" -> ").append(edge.getAimNode().getDotNum()).append("[color=").append(edge.getType().getColor()).append("];");
                }

            }


        }

        for (GraphEdge edge : this.allDFGEdgesList) {
            // 如果起始点的dotnum是null 说明是日志语句
            // 日志语句的DFG忽略就可以了
            if (edge.getOriginalNode().getDotNum() == null || edge.getAimNode().getDotNum() == null) {
                continue;
            }
            str.append(System.lineSeparator() + edge.getOriginalNode().getDotNum() + " -> " + edge.getAimNode().getDotNum() + "[color=" + edge.getType().getColor() + "];");
        }

        if (ncs) {
            NCS(leafNodes);
        }
        str.append(System.lineSeparator() + "}");
    }

    private void dfs(AstNode node, String parentNodeName, StringBuilder str, StringBuilder sentence) {
        if (node != null) {

            List<String> attributes = node.getAttributes();
            List<AstNode> subNodes = node.getSubNodes();
            List<String> subLists = node.getSubLists();
            List<List<AstNode>> subListNodes = node.getSubListNodes();

            String ndName = "n" + (index2++);

            if (!node.toString().equals("")) {
                String label = DotPrintFilter.AstNodeFilter(node.getTypeName());
                sentence.append(DotPrintFilter.cut(label)).append(' ');
                str.append(System.lineSeparator()).append(ndName).append(" [label=\"").append(DotPrintFilter.AstNodeFilter(node.getTypeName())).append("\", ast_node=\"true\"];");
            }

            if (!parentNodeName.isEmpty()) {
                str.append(System.lineSeparator()).append(parentNodeName).append(" -> ").append(ndName).append("[color=").append(EdgeTypes.AST.getColor()).append("];");
            }

            for (String a : attributes) {
                String label = DotPrintFilter.AstNodeFilter(a);
                sentence.append(DotPrintFilter.cut(label)).append(' ');
                String attrName = "n" + (index2++);
                str.append(System.lineSeparator()).append(attrName).append(" [label=\"").append(DotPrintFilter.AstNodeFilter(a)).append("\", ast_node=\"true\"];");
                str.append(System.lineSeparator()).append(ndName).append(" -> ").append(attrName).append("[color=").append(EdgeTypes.AST.getColor()).append("];");
            }

            for (AstNode subNode : subNodes) {
                dfs(subNode, ndName, str, sentence);
            }

            for (int i = 0; i < subLists.size(); i++) {
                String label = DotPrintFilter.AstNodeFilter(subLists.get(i));
                sentence.append(DotPrintFilter.cut(label)).append(' ');

                String ndLstName = "n" + (index2++);
                str.append(System.lineSeparator()).append(ndLstName).append(" [label=\"").append(DotPrintFilter.AstNodeFilter(subLists.get(i))).append("\", ast_node=\"true\"];");
                str.append(System.lineSeparator()).append(ndName).append(" -> ").append(ndLstName).append("[color=").append(EdgeTypes.AST.getColor()).append("];");

                for (int j = 0; j < subListNodes.get(i).size(); j++) {
                    dfs(subListNodes.get(i).get(j), ndLstName, str, sentence);
                }

            }


        }
    }

    public void NCS(List<String> allLeafNodes) {
        if (allLeafNodes.size() > 1) {
            for (int i = 1; i < allLeafNodes.size(); i++) {
                str.append(System.lineSeparator() + allLeafNodes.get(i - 1) + " -> " + allLeafNodes.get(i) + "[color=" + EdgeTypes.NCS.getColor() + "];");
            }
        }
    }

    public void print(GraphNode root, String methodName, String uniqueMethodName, boolean ncs) {
        BFS(root, ncs);

        File file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
        }
        try {
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(
                    new File(path + "/" + uniqueMethodName + "_CA.dot")));
            bufferedWriter.write(str.toString());
            bufferedWriter.flush();
            bufferedWriter.close();
        } catch (Exception e) {
            System.out.println("数据写入ast文件发送异常！");
        }


        // 我写的
        try {
            File ASTfile = new File(path + "/statements@" + uniqueMethodName + ".dot");
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(ASTfile));
            for (Map.Entry<String, String> entry : ASTStrMap.entrySet()) {
                String filecontent = entry.getValue();
                bufferedWriter.write(filecontent);
                bufferedWriter.write("\n");
            }
            bufferedWriter.flush();
            bufferedWriter.close();

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("数据写入ast文件发送异常！");
        }


        try (FileWriter writer = new FileWriter(MConfig.rootDir + MConfig.rawDir + MConfig.projectName + "/" + MConfig.projectName + "_corpus.txt", true);
             BufferedWriter bw = new BufferedWriter(writer)) {
            for (String sentence : sentences) {
                bw.append(sentence);
                bw.newLine();
            }
        } catch (Exception e) {
            System.out.println("数据写入语料库文件异常！");
        }


    }

    private GraphNode findNotLogAncestor(GraphNode node) {
        while (node != null) {
            if (!LogUtil.isLogStatement(node.getOriginalCodeStr(), 1)) {
                return node;
            }

            if (!node.getPreAdjacentPoints().isEmpty()) {
                node = node.getPreAdjacentPoints().get(0);
            } else node = node.getParentNode();
        }
        return node;
    }
}
