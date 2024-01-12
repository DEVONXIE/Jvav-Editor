import javax.swing.*;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseEvent;

import java.io.*;

public class Editor extends JFrame {
    private LineNumberedTextArea textArea;
    private JTree foldTree;
    private static DefaultTreeModel treeModel;
    private static File currentFile;
    private static JTextArea outputTextArea;
    private StringBuilder compileErrorMessage = new StringBuilder();

    public Editor() {
        super("jvav Editor");
        setSize(1920, 1080);
        textArea = new LineNumberedTextArea();
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 15));
        JScrollPane scrollPane = new JScrollPane(textArea);
        add(scrollPane, BorderLayout.CENTER);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        outputTextArea = new JTextArea(10, 40);
        outputTextArea.setEditable(false);
        outputTextArea.setFont(new Font("Monospaced", Font.PLAIN, 15));
        JScrollPane outputScrollPane = new JScrollPane(outputTextArea);
        add(outputScrollPane, BorderLayout.SOUTH);
        JMenuBar menuBar = new JMenuBar();
        setJMenuBar(menuBar);
        JMenu filMenu = new JMenu("File");
        menuBar.add(filMenu);
        JMenuItem openFile = new JMenuItem("Open File");
        openFile.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setDialogTitle("Choose your file");
                fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
                fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
                int result = fileChooser.showOpenDialog(scrollPane);
                if (result == JFileChooser.APPROVE_OPTION) {
                    File selectedfFile = fileChooser.getSelectedFile();
                    currentFile = selectedfFile;
                    openFile(selectedfFile);
                }
            }
        });
        filMenu.add(openFile);
        JMenuItem saveAsMenuItem = new JMenuItem("Save As");
        saveAsMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setDialogTitle("Save File");
                fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
                fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
                int result = fileChooser.showSaveDialog(scrollPane);
                if (result == JFileChooser.APPROVE_OPTION) {
                    File selecFile = fileChooser.getSelectedFile();
                    currentFile = selecFile;
                    try {
                        FileWriter fileWriter = new FileWriter(selecFile);
                        fileWriter.write(textArea.getText());
                        fileWriter.close();
                        outputTextArea.setText("File save successfully.\n");
                    } catch (IOException ex) {
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(Editor.this, "Error saving the file", "Error",
                                JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });
        JMenuItem openFloder = new JMenuItem("Open Floder");
        openFloder.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openFloder(scrollPane);
            }
        });
        filMenu.add(openFloder);
        filMenu.add(saveAsMenuItem);
        DefaultMutableTreeNode root = new DefaultMutableTreeNode();
        treeModel = new DefaultTreeModel(root);
        foldTree = new JTree(treeModel);
        foldTree.setRootVisible(true);
        foldTree.addTreeSelectionListener(e -> {
            TreePath selectedPath = e.getNewLeadSelectionPath();
            if (selectedPath != null) {
                DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) selectedPath.getLastPathComponent();
                DefaultMutableTreeNode rootNode = (DefaultMutableTreeNode) foldTree.getModel().getRoot();
                File selectedFile = (File) selectedNode.getUserObject();
                // 检查选中的节点是否为根节点
                if (!selectedNode.equals(rootNode) && selectedNode.getUserObject() instanceof File) {

                    // 检查是否为文件夹且未展开
                    if (selectedFile.isDirectory() && !foldTree.isExpanded(selectedPath)) {
                        foldTree.expandPath(selectedPath);
                    } else {
                        openFile(selectedFile);
                    }
                }

            }
        });
        // 添加侧边栏
        JScrollPane siderBar = new JScrollPane(foldTree);
        siderBar.setPreferredSize(new Dimension(200, 600));
        siderBar.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        getContentPane().add(siderBar, BorderLayout.WEST);
        JMenuItem saveFile = new JMenuItem("Save");
        saveFile.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                saveFile();
            }
        });
        filMenu.add(saveFile);
        JMenu buildMenu = new JMenu("Build");
        menuBar.add(buildMenu);
        JMenuItem compilemItem = new JMenuItem("Compile");
        compilemItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                compile();
            }
        });
        buildMenu.add(compilemItem);
        JMenuItem compileAndRunItem = new JMenuItem("Compile and Run");
        compileAndRunItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                compileAndRun();
            }
        });
        buildMenu.add(compileAndRunItem);
    }

    private void openFloder(JScrollPane scrollPane) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Choose your Floder");
        fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);// 只能打开文件夹
        int result = fileChooser.showOpenDialog(scrollPane);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFolder = fileChooser.getSelectedFile();
            updateFileTree(selectedFolder);
        }
    }

    // 编译并运行函数
    private void compileAndRun() {
        compile();
        if (compileErrorMessage != null) {
            outputTextArea.setText(compileErrorMessage.toString());
        } else {
            if (currentFile != null) {
                String filePath = currentFile.getAbsolutePath();
                try {
                    String className = currentFile.getName().replace(".java", "");
                    ProcessBuilder processBuilder = new ProcessBuilder("CMD.exe", "/C", "start", "java", className);
                    // ProcessBuilder processBuilder = new ProcessBuilder("java", className);
                    processBuilder.directory(new File(filePath).getParentFile());
                    processBuilder.redirectErrorStream(true);
                    Process process = processBuilder.start();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                    StringBuilder output = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        output.append(line).append('\n');
                    }
                    int exitCode = process.waitFor();
                    String resultMessage = exitCode == 0 ? "Build and Run successful.\n"
                            : "Build and Run failed:\n";
                    output.append(resultMessage);
                    outputTextArea.setText(output.toString());
                } catch (Exception e) {
                    e.printStackTrace();
                    outputTextArea.setText("Error during Build and Run.");
                }
            } else {
                outputTextArea.setText("No file opened to Build and Run.");
            }
        }

    }

    // 用ProcessBuilder执行编译命令并把结果显示在底边栏
    private void compile() {
        outputTextArea.setText("");
        saveFile();
        if (currentFile != null) {
            String filePath = currentFile.getAbsolutePath();
            try {
                ProcessBuilder processBuilder = new ProcessBuilder("javac", filePath);
                processBuilder.directory(new File(filePath).getParentFile());
                processBuilder.redirectErrorStream(true);
                Process process = processBuilder.start();
                BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                StringBuilder output = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    output.append(line).append('\n');
                }
                int exitCode = process.waitFor();
                String resultMessage = exitCode == 0 ? "Compilation successful.\n" : "Compilation failed:\n";
                output.append(resultMessage);
                outputTextArea.setText(output.toString());
                compileErrorMessage.setLength(0);
                compileErrorMessage.append(output);
            } catch (Exception e) {
                e.printStackTrace();
                outputTextArea.setText("Error during compilation.\n");
            }
        } else {
            outputTextArea.setText("No file opened to compile.\n");
        }
    }

    // 递归创建文件树
    private static void updateFileTree(File folder) {
        DefaultMutableTreeNode root = new DefaultMutableTreeNode(folder);
        creatNodes(root, folder);
        treeModel.setRoot(root);
    }

    // 创建节点，也是递归
    private static void creatNodes(DefaultMutableTreeNode root, File folder) {
        if (folder.isDirectory()) {
            File[] files = folder.listFiles();
            if (files != null) {
                for (File file : files) {
                    DefaultMutableTreeNode node = new DefaultMutableTreeNode(file);
                    root.add(node);
                    creatNodes(node, file);
                }
            }
        }
    }

    private void saveFile() {
        if (currentFile != null) {
            try {
                BufferedWriter writer = new BufferedWriter(new FileWriter(currentFile));
                writer.write(textArea.getText());
                outputTextArea.setText("File save successfully.\n");
                writer.close();
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error saving file.");
            }
        } else {
            JOptionPane.showMessageDialog(this, "No file opening");
        }

    }

    private void openFile(File selectedFile) {
        try {
            FileReader fileReader = new FileReader(selectedFile);
            currentFile = selectedFile;
            BufferedReader bufferedReader = new BufferedReader((fileReader));
            StringBuilder stringBuilder = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line).append("\n");
            }
            textArea.setText(stringBuilder.toString());
            bufferedReader.close();
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(Editor.this, "Error reading the file", "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Editor jvav = new Editor();
            jvav.setVisible(true);
        });
    }
}
