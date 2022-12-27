package view;

import model.GraphEdge;
import model.GraphNode;
import utils.DotPrintFilter;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

public class CFG_DFGPrinter {

    private String path;
    private Set<GraphEdge> allDFGEdgesList;

    public CFG_DFGPrinter(String path, Set<GraphEdge> allDFGEdgesList) {
        this.path = path;
        this.allDFGEdgesList = allDFGEdgesList;
    }

    private String BFS(GraphNode root) {
        int index = 0;
        StringBuilder str = new StringBuilder("digraph {");
        Queue<GraphNode> dealingNodes = new LinkedList<>();
        dealingNodes.add(root);
        while (!dealingNodes.isEmpty()) {
            GraphNode par = dealingNodes.poll();
            String parIndexNum = "";
            if (par.getDotNum() != null) {
                parIndexNum = par.getDotNum();
            } else {
                parIndexNum = "n" + (index++);
                par.setDotNum(parIndexNum);
                str.append(System.lineSeparator() + par.getDotNum() + " [label=\"" + DotPrintFilter.filterQuotation(par.getOriginalCodeStr()) + "\" , line=" + par.getCodeLineNum() + ", hasException=\" " + par.getIsExceptionLabel() + "\"];");
            }
            List<GraphNode> adjacentPoints = par.getAdjacentPoints();
            for (GraphNode child : adjacentPoints) {
                if (child.getDotNum() == null) {
                    dealingNodes.add(child);
                    child.setDotNum("n" + (index));
                    index++;
                    str.append(System.lineSeparator() + child.getDotNum() + " [label=\"" + DotPrintFilter.filterQuotation(child.getOriginalCodeStr()) + "\" , line=" + child.getCodeLineNum() + ", hasException=\" " + child.getIsExceptionLabel() + "\"];");
                }
            }
            for (GraphEdge edge : par.getEdgs()) {
                str.append(System.lineSeparator() + edge.getOriginalNode().getDotNum() + " -> " + edge.getAimNode().getDotNum() + "[color=" + edge.getType().getColor() + "];");
            }
        }

        for (GraphEdge edge : this.allDFGEdgesList) {
            str.append(System.lineSeparator() + edge.getOriginalNode().getDotNum() + " -> " + edge.getAimNode().getDotNum() + "[color=" + edge.getType().getColor() + "];");
        }

        str.append(System.lineSeparator() + "}");
        return str.toString();
    }

    public void print(GraphNode root, String methodName, String methodParms) {
        String bfs = BFS(root);
//        if(!new File(path).exists() && new File(path).isDirectory()){
        File file = new File(path); //以某路径实例化一个File对象
        if (!file.exists()) {
            file.mkdirs();
        }
        try {
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(
                    new File(path + "/" + methodName + "." + methodParms + "_DFG.dot")));
            bufferedWriter.write(bfs);
            bufferedWriter.flush();
            bufferedWriter.close();
        } catch (Exception e) {
            System.out.println("数据写入ast文件发送异常！");
            e.printStackTrace();
        }
    }
}
