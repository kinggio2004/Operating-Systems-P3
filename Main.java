import java.io.IOException;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import java.nio.file.*;
import java.util.List;
import java.nio.file.Path;
import java.nio.file.Paths;
import javafx.scene.control.TreeItem;

public class Main extends Application {
	  private TreeItem<String> rootItem; 
	  private Path currentRootPath = Paths.get(System.getProperty("user.home"), "Desktop", "TestFolder");
	  private FileService fileService = new FileService(); 
	  private TextArea fileEditor = new TextArea();
      private Label statusLabel = new Label("Status: Ready"); 

    @Override
    public void start(Stage primaryStage) {
        // 1. Layout Components
        BorderPane root = new BorderPane();
        
     // Path to a folder you want to manage (e.g., a "TestFolder" on your desktop)
        Path startPath = Paths.get(System.getProperty("user.home"), "Desktop", "TestFolder");

        rootItem = new TreeItem<>(startPath.toString());
        rootItem.setExpanded(true);
     // Utilizing DirectoryStream for memory-efficient iteration over large file systems
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(startPath)) {
            for (Path entry : stream) {
                rootItem.getChildren().add(new TreeItem<>(entry.getFileName().toString()));
            }
        } catch (IOException e) {
            showAlert("Error", "Could not load directory: " + e.getMessage());
        }
        
        // Navigation Tree (Left side)
        TreeView<String> treeView = new TreeView<>();
        
        treeView.setRoot(rootItem);
        
        // Buttons (Top side)
        Button btnSave = new Button("Save Changes");
        Button btnDelete = new Button("Delete File");
        
        Button btnRefresh = new Button("Refresh");  
        Button btnProperties = new Button("Properties"); // <--- CREATE THE NEW BUTTON

        // Add all three buttons to the HBox
        Button btnRename = new Button("Rename File");
        HBox topMenu = new HBox(10, btnRefresh, btnSave, btnDelete, btnProperties, btnRename); 
     
     // REFRESH LOGIC (Links the button to your method)
        btnRefresh.setOnAction(e -> handleRefresh());

        // 2. Add Delete Button Logic
        btnDelete.setOnAction(e -> {
            TreeItem<String> selected = treeView.getSelectionModel().getSelectedItem();
            if (selected != null) {
                try {
                    // Reconstruct path to TestFolder
                    Path pathToDelete = Paths.get(System.getProperty("user.home"), "Desktop", "TestFolder", selected.getValue());
                    
                    // Attempt the OS delete call
                    Files.delete(pathToDelete);
                    
                    // Update the UI
                    statusLabel.setText("Status: " + selected.getValue() + " deleted.");
                    rootItem.getChildren().remove(selected); // Removes it from the visual tree
                    
                } catch (java.nio.file.AccessDeniedException ex) {
                    showAlert("Permission Denied", "The OS refused to delete this file. It might be Read-Only.");
                } catch (IOException ex) {
                    showAlert("Delete Error", "Could not delete file: " + ex.getMessage());
                }
            } else {
                statusLabel.setText("Status: Please select a file from the list first!");
            }
        });

     // RENAME BUTTON LOGIC
        btnRename.setOnAction(e -> {
            TreeItem<String> selected = treeView.getSelectionModel().getSelectedItem();
            
            if (selected != null) {
                // 1. Create a simple dialog to ask for the new name
                TextInputDialog dialog = new TextInputDialog(selected.getValue());
                dialog.setTitle("Rename File");
                dialog.setHeaderText("Enter a new name for: " + selected.getValue());
                dialog.setContentText("New name:");

                dialog.showAndWait().ifPresent(newName -> {
                    try {
                        Path source = Paths.get(System.getProperty("user.home"), "Desktop", "TestFolder", selected.getValue());
                        
                        // 2. Call your atomic rename method
                        fileService.renameFile(source, newName);
                        
                        // 3. Update UI if successful
                        selected.setValue(newName);
                        statusLabel.setText("Status: Renamed to " + newName);
                        
                    } catch (Exception ex) {
                        // 4. THIS IS THE STRESS TEST CATCH
                        showAlert("Naming Error", "The OS rejected this name: " + ex.getMessage());
                    }
                });
            } else {
                statusLabel.setText("Status: Select a file to rename first!");
            }
        });
        // --- SAVE BUTTON LOGIC ---
        btnSave.setOnAction(e -> {
            // 1. Get the selected item from the TreeView
            TreeItem<String> selected = treeView.getSelectionModel().getSelectedItem();
            
            if (selected != null) {
                try {
                    // 2. Reconstruct the full path to the file in your TestFolder
                    Path path = Paths.get(System.getProperty("user.home"), "Desktop", "TestFolder", selected.getValue());

                    // 3. Call your fileService to write the text from the Editor
                    fileService.updateFile(path, fileEditor.getText());
                    
                    statusLabel.setText("Status: Changes saved to " + selected.getValue());
                } catch (Exception ex) {
                    // This will catch the "File In Use" or "Access Denied" errors!
                    showAlert("Save Error", "Could not save file: " + ex.getMessage());
                }
            } else {
                statusLabel.setText("Status: Please select a file from the tree to save.");
            }
        });

        
        // 3. Put it all together
        root.setLeft(treeView);
        root.setCenter(fileEditor);
        root.setTop(topMenu);
        root.setBottom(statusLabel);

        Scene scene = new Scene(root, 900, 600);
        primaryStage.setTitle("OS File Manager");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
 
    private void handleRefresh() {
        try {
            rootItem.getChildren().clear();
            List<Path> updatedFiles = fileService.refreshDirectory(currentRootPath);
            
            for (Path file : updatedFiles) {
                rootItem.getChildren().add(new TreeItem<>(file.getFileName().toString()));
            }
            statusLabel.setText("System state synchronized.");
        } catch (IOException e) {
            showAlert("Refresh Error", e.getMessage());
        }
    }

    
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null); // Keeps it simple
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
    
}