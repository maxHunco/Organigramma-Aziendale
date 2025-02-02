package classi.GUI;

import javax.swing.*;
import javax.swing.tree.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import classi.*;
import classi.Component;

public class OrgTreeView extends JPanel {
    private JTree tree;
    private OrgController controller;

    public OrgTreeView(OrgController controller) {
        this.controller = controller;
        this.setLayout(new BorderLayout());
        tree = new JTree(createTreeNode(controller.getRoot()));
        TreeSelectionModel selectionModel = tree.getSelectionModel();
        selectionModel.setSelectionMode(TreeSelectionModel.CONTIGUOUS_TREE_SELECTION);
        tree.setSelectionModel(selectionModel);
        JScrollPane treeView = new JScrollPane(tree);
        this.add(treeView, BorderLayout.CENTER);
    }

    private DefaultMutableTreeNode createTreeNode(Component component) {
        DefaultMutableTreeNode node = new DefaultMutableTreeNode(component.getName());
        if (component instanceof WorkUnit) {
            WorkUnit workUnit = (WorkUnit) component;

            // Aggiunge i dipendenti come nodi sotto la WorkUnit
            for (Component child : workUnit.getChildren()) {
                if (child instanceof Employee) {
                    Employee employee = (Employee) child;
                    node.add(new DefaultMutableTreeNode(employee.getRoles()+" : "+employee.getName()));
                } else {
                    // Aggiunge unità lavorative figlie
                    node.add(createTreeNode(child));
                }
            }
        }
        return node;
    }

    public void refreshTree() {
        // Ricrea l'albero con i dati aggiornati
        tree.setModel(new DefaultTreeModel(createTreeNode(controller.getRoot())));
        tree.updateUI();  // Forza il refresh dell'albero
    }
}
