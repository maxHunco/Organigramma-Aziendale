package classi.GUI;

import classi.db.dbHandler;
import classi.OrgController;

import javax.swing.*;
import java.awt.*;
import java.io.File;

public class MainFrame extends JFrame {
    private OrgController controller;
    private dbHandler handler;
    private OrgTreeView treeView;
    private ControlPanel controlPanel;

    public MainFrame() {
        // Mostra il file chooser all'avvio
        File selectedFile = showFileChooser();
        if (selectedFile == null) {
            handler = new dbHandler();
            controller = new OrgController();
            initializeUI();
        }
        else{
            // Inizializza il controller e il db basato sul file selezionato
            handler = new dbHandler();
            controller = handler.inizializzaDB(selectedFile);

            // Inizializza l'interfaccia utente
            initializeUI();
        }
    }

    private void initializeUI() {
        setTitle("Company Organization Manager");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Creazione dei componenti
        treeView = new OrgTreeView(controller);
        controlPanel = new ControlPanel(controller, treeView);

        // Aggiunta dei componenti al layout
        add(treeView, BorderLayout.CENTER);
        add(controlPanel, BorderLayout.SOUTH);

        setSize(800, 600);
        setLocationRelativeTo(null);
    }

    private File showFileChooser() {
        // Crea un file chooser
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Seleziona un file .sql");
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("SQL Files", "sql"));

        // Mostra il dialogo e cattura il risultato
        int result = fileChooser.showOpenDialog(null);

        // Verifica se un file è stato selezionato
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            // Controlla che il file abbia l'estensione corretta
            if (selectedFile.getName().endsWith(".sql")) {
                return selectedFile;
            } else {
                JOptionPane.showMessageDialog(null, "Il file selezionato non è valido. Deve essere un file .sql.",
                        "Errore", JOptionPane.ERROR_MESSAGE);
            }
        }
        return null; // Nessun file valido selezionato
    }
}
