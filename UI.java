package filemanager;


import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.util.ArrayList;

public class UI extends JFrame {
    private JPanel catalogPanel = new JPanel();
    private JList filesList = new JList();
    private JScrollPane filesScroll = new JScrollPane(filesList);
    private JPanel buttonsPanel = new JPanel();
    private JButton addButton = new JButton("Создать папку");
    private JButton backButton = new JButton("Назад");
    private JButton deleteButton = new JButton("Удалить");
    private JButton renameButton = new JButton("Переименовать");
    private ArrayList<String> dirsCash = new ArrayList<String>(); // стек для пути

    public UI() {
        super("Проводник"); // наследование
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // работа приложения остановлена
        setResizable(true); // можно менять размер
        catalogPanel.setLayout(new BorderLayout(5,5));
        catalogPanel.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
        buttonsPanel.setLayout(new GridLayout(1,4,5,5));
        JDialog createNewDirectory = new JDialog(UI.this, "Создание папки", true);
        File discs[] = File.listRoots(); // массив корневых каталогов
        filesScroll.setPreferredSize(new Dimension(400,500)); // чтобы окно не сжималось при маленьком числе элементов
        filesList.setListData(discs);
        filesList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION); // можно выбирать

        filesList.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if(e.getClickCount() == 2) {
                    DefaultListModel model = new DefaultListModel();
                    String selectedObject = filesList.getSelectedValue().toString();
                    String fullpath = toFullPath(dirsCash);
                    File selectedFile;
                    if (dirsCash.size() > 1) {
                        selectedFile = new File(fullpath,selectedObject);
                    }else{
                        selectedFile = new File(fullpath + selectedObject);
                    }

                    if(selectedFile.isDirectory()) {
                        String [] rootstr = selectedFile.list();
                        for (String str: rootstr) {
                            File checkObject = new File(selectedFile.getPath(), str);
                            if(!checkObject.isHidden()) {
                                model.addElement(str);
                            }
                        }

                        dirsCash.add(selectedObject);
                        filesList.setModel(model);
                    }
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {

            }

            @Override
            public void mouseReleased(MouseEvent e) {

            }

            @Override
            public void mouseEntered(MouseEvent e) {

            }

            @Override
            public void mouseExited(MouseEvent e) {

            }
        });
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(dirsCash.size() > 1) {
                    dirsCash.remove(dirsCash.size()-1);
                    String backDir = toFullPath(dirsCash);
                    String[] objects = new File(backDir).list();
                    DefaultListModel backRootModel = new DefaultListModel();

                    for(String str: objects) {
                        File checkFile = new File(backDir, str);
                        if (!checkFile.isHidden()) {
                            backRootModel.addElement(str);
                        }
                    }
                    filesList.setModel(backRootModel);
                }else {
                    dirsCash.removeAll(dirsCash);
                    filesList.setListData(discs);
                }
            }
        });
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(!dirsCash.isEmpty()) {
                    String currentPath;
                    File newFolder;
                    CreateNewFolderJDialog newFolderJDialog = new CreateNewFolderJDialog(UI.this);

                    if (newFolderJDialog.getReady()) {
                        currentPath = toFullPath(dirsCash);
                        newFolder = new File(currentPath, newFolderJDialog.getNewName());
                        if(!newFolder.exists())
                            newFolder.mkdir();

                        File updateDir = new File(currentPath);
                        String updateMas[] = updateDir.list();
                        DefaultListModel updateModel = new DefaultListModel();
                        for(String str: updateMas) {
                            File check = new File(updateDir.getPath(), str);
                            if(!check.isHidden()){
                                updateModel.addElement(str);
                            }
                        }
                        filesList.setModel(updateModel);
                    }
                }
            }
        });
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selectedObject = filesList.getSelectedValue().toString();
                String currentPath = toFullPath(dirsCash);
                if(!selectedObject.isEmpty()){
                    deleteDir(new File(currentPath, selectedObject));

                    File updateDir = new File(currentPath);
                    String updateMas[] = updateDir.list();
                    DefaultListModel updateModel = new DefaultListModel();

                    for(String str: updateMas){
                        File check = new File (updateDir.getPath(), str);
                        if(!check.isHidden()){
                            updateModel.addElement(str);
                        }
                    }
                    filesList.setModel(updateModel);
                }
            }
        });
        renameButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(!dirsCash.isEmpty() & filesList.getSelectedValue() != null){
                    String currentPath = toFullPath(dirsCash);
                    String selectedObject = filesList.getSelectedValue().toString();
                    RenameJDialog renamer = new RenameJDialog(UI.this);
                    if(renamer.getReady()) {
                        File renameFile = new File(currentPath, selectedObject);
                        renameFile.renameTo(new File(currentPath, renamer.getNewName()));

                        File updateDir = new File(currentPath);
                        String updateMas[] = updateDir.list();
                        DefaultListModel updateModel = new DefaultListModel();
                        for (String str : updateMas) {
                            File check = new File(updateDir.getPath(), str);
                            if (!check.isHidden()) {
                                updateModel.addElement(str);
                            }
                        }
                        filesList.setModel(updateModel);
                    }
                }
            }
        });

        buttonsPanel.add(backButton);
        buttonsPanel.add(addButton);
        buttonsPanel.add(deleteButton);
        buttonsPanel.add(renameButton);
        catalogPanel.setLayout(new BorderLayout());
        catalogPanel.add(filesScroll, BorderLayout.CENTER);
        catalogPanel.add(buttonsPanel, BorderLayout.SOUTH);

        getContentPane().add(catalogPanel);
        setSize(600, 600);
        setLocationRelativeTo(null); // по центру экрана
        setVisible(true); // посмотреть
    }
    public String toFullPath(ArrayList<String> file) {
        String listPart = "";
        for(String str : file)
            listPart += "\\" + str;
        return listPart;
    }
    public void deleteDir(File file){
        File[] objects = file.listFiles();
        if(objects != null){
            for(File f : objects){
                deleteDir(f);
            }
        }
        file.delete();
    }
}
