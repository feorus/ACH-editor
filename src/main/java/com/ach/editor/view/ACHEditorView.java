/*
 * ACHViewer.java
 *
 * Created on April 15, 2007, 11:16 AM
 */

package com.ach.editor.view;

import java.awt.Cursor;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import com.ach.domain.ACHBatch;
import com.ach.domain.ACHEntry;
import com.ach.domain.ACHDocument;
import com.ach.domain.ACHRecord;
import com.ach.domain.ACHRecordAddenda;
import com.ach.domain.ACHSelection;
import com.ach.editor.model.ACHEditorModel;
import com.ach.editor.model.ModelListener;

/**
 * 
 * @author John
 */
public class ACHEditorView extends javax.swing.JFrame implements ModelListener {

    private static final long serialVersionUID = 0;
	
	// Variables declaration - do not modify//GEN-BEGIN:variables
	private javax.swing.JCheckBoxMenuItem jCheckBoxMenuFedFile;

	private javax.swing.JLabel jLabel1;

	private javax.swing.JLabel jLabel2;

	private javax.swing.JLabel jLabelAchInfoBatchCount;

    private javax.swing.JLabel jLabelAchInfoBatchCountText;

	private javax.swing.JLabel jLabelAchInfoEntryCount;

	private javax.swing.JLabel jLabelAchInfoEntryCountText;

	private javax.swing.JLabel jLabelAchInfoFileCreation;

	private javax.swing.JLabel jLabelAchInfoFileCreationText;

	private javax.swing.JLabel jLabelAchInfoFileName;

	private javax.swing.JLabel jLabelAchInfoFileNameText;

	private javax.swing.JLabel jLabelAchInfoImmDest;

    private javax.swing.JLabel jLabelAchInfoImmDestText;

    private javax.swing.JLabel jLabelAchInfoImmOrigin;

	private javax.swing.JLabel jLabelAchInfoImmOriginText;

	private javax.swing.JLabel jLabelAchInfoTotalCredit;

	private javax.swing.JLabel jLabelAchInfoTotalCreditText;

	private javax.swing.JLabel jLabelAchInfoTotalDebit;

	private javax.swing.JLabel jLabelAchInfoTotalDebitText;

	private javax.swing.JList jListAchDataAchRecords;

	private javax.swing.JMenuBar jMenuBar;

	private javax.swing.JMenu jMenuFile;

	private javax.swing.JMenuItem jMenuItemFileExit;

	private javax.swing.JMenuItem jMenuItemFileNew;

	private javax.swing.JMenuItem jMenuItemFileOpen;

	private javax.swing.JMenuItem jMenuItemFileSave;

	private javax.swing.JMenuItem jMenuItemFileSaveAs;

	private javax.swing.JMenuItem jMenuItemMulitpleDelete;

	private javax.swing.JMenuItem jMenuItemPopupAddendaCopy;

	private javax.swing.JMenuItem jMenuItemPopupAddendaDelete;

	private javax.swing.JMenuItem jMenuItemPopupAddendaPaste;

	private javax.swing.JMenuItem jMenuItemPopupBatchAdd;

	private javax.swing.JMenuItem jMenuItemPopupBatchAddEntry;

	private javax.swing.JMenuItem jMenuItemPopupBatchCopy;

	private javax.swing.JMenuItem jMenuItemPopupBatchDeleteBatch;

	private javax.swing.JMenuItem jMenuItemPopupBatchEditBatch;

	private javax.swing.JMenuItem jMenuItemPopupBatchPaste;

	private javax.swing.JMenuItem jMenuItemPopupEntryAdd;

	private javax.swing.JMenuItem jMenuItemPopupEntryAddAddenda;

	private javax.swing.JMenuItem jMenuItemPopupEntryAddendaAdd;

	private javax.swing.JMenuItem jMenuItemPopupEntryCopy;

	private javax.swing.JMenuItem jMenuItemPopupEntryDelete;

	private javax.swing.JMenuItem jMenuItemPopupEntryEditEntry;

	private javax.swing.JMenuItem jMenuItemPopupEntryPaste;

	private javax.swing.JMenuItem jMenuItemPopupFileAddBatch;

	private javax.swing.JMenuItem jMenuItemPopupFileEdit;

	private javax.swing.JMenuItem jMenuItemPopupMultipleCopy;

	private javax.swing.JMenuItem jMenuItemPopupPasteMultiple;

	private javax.swing.JMenuItem jMenuItemToolsRecalculate;

	private javax.swing.JMenuItem jMenuItemToolsReverse;

	private javax.swing.JMenuItem jMenuItemToolsValidate;

	private javax.swing.JMenu jMenuTools;

	private javax.swing.JPanel jPanel1;

	public javax.swing.JPopupMenu jPopupMenuAddenda;

	public javax.swing.JPopupMenu jPopupMenuBatch;

	public javax.swing.JPopupMenu jPopupMenuEntry;

	public javax.swing.JPopupMenu jPopupMenuFile;

	public javax.swing.JPopupMenu jPopupMenuMultipleSelection;

	private javax.swing.JScrollPane jScrollPane1;

	private javax.swing.JSeparator jSeparatorMenuFile;

	private ACHEditorModel model;

	private Point mouseClick = null;

	private Vector<Integer[]> positions = new Vector<Integer[]>(10, 10);

	/** Creates new form ACHViewer */
	public ACHEditorView(ACHEditorModel achappmodel) {

		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			// Don't throw errors if the native look and feel can't be found,
			// just go with what we have
		} catch (UnsupportedLookAndFeelException e) {
		} catch (ClassNotFoundException e) {
		} catch (InstantiationException e) {
		} catch (IllegalAccessException e) {
		}

	    initComponents();

		jMenuItemToolsRecalculate.setEnabled(false);
		jMenuItemToolsValidate.setEnabled(false);

		setLocationRelativeTo(null); // Centers the window on the screen

		clearAchInfo();

		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

		
		this.model = achappmodel;
		this.model.addSubscriber(this);
	}

	private synchronized void addJListAchDataAchRecordsItem(ACHRecord achRecord) {
		((DefaultListModel) jListAchDataAchRecords.getModel())
				.addElement(achRecord);
	}

	public int askDeleteCancel(final String title, final String message) {
        Object[] options = { "Delete", DoYouWantToSaveTheChangesDialogOptions.CANCEL };
        int selection = JOptionPane.showOptionDialog(this,
                message,
                title, JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE,
                null, options, options[0]);
        return selection;
    }

	public DoYouWantToSaveTheChangesDialogOptions askDoYouWantSaveChanges() {
	    DoYouWantToSaveTheChangesDialogOptions[] options = { DoYouWantToSaveTheChangesDialogOptions.DONT_SAVE, DoYouWantToSaveTheChangesDialogOptions.CANCEL, DoYouWantToSaveTheChangesDialogOptions.SAVE };
        int selection = JOptionPane.showOptionDialog(this, "ACH File has been changed. What would you like to do.",
                "ACH File has changed", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options,
                options[2]);
        return options[selection];
    }

	public int askYesNo(final String message, final String title) {
        Object[] options = { "Yes", "No" };
        int answer = JOptionPane.showOptionDialog(this, message,
                title, JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options,
                options[1]);
        return answer;
    }

	private void clearAchInfo() {
		jLabelAchInfoFileName.setText("");
		jLabelAchInfoFileCreation.setText("");
		jLabelAchInfoImmDest.setText("");
		jLabelAchInfoImmOrigin.setText("");
		jLabelAchInfoBatchCount.setText("0");
		jLabelAchInfoEntryCount.setText("0");
		jLabelAchInfoTotalDebit.setText("0");
		jLabelAchInfoTotalCredit.setText("0");
		jListAchDataAchRecords.setModel(new DefaultListModel());
	}

	public synchronized void clearJListAchDataAchRecords() {
		((DefaultListModel) jListAchDataAchRecords.getModel())
				.removeAllElements();
		jMenuItemToolsRecalculate.setEnabled(false);
		jMenuItemToolsValidate.setEnabled(false);
	}

	/**
     * @return
     */
    public int clickedIndex() {
        return jListAchDataAchRecords.locationToIndex(mouseClick);
    }

	// This method writes the current selection to the system clipboard
	public void copy(int[] selected) {
		Vector<ACHRecord> achRecords = new Vector<ACHRecord>(selected.length,
				10);
		for (int i = 0; i < selected.length; i++) {
			achRecords.add(getRow(i));
		}
		ACHSelection clipboardSelection = new ACHSelection(achRecords);
		Toolkit.getDefaultToolkit().getSystemClipboard().setContents(
				clipboardSelection, null);
	}

	// If a ACHSelection type is on the system clipboard, this method returns
	// it;
	// otherwise it returns null.
	@SuppressWarnings("unchecked")
    public Vector<ACHRecord> getClipboard() {
		Transferable t = Toolkit.getDefaultToolkit().getSystemClipboard()
				.getContents(null);

		try {
			if (t != null
					&& t.isDataFlavorSupported(ACHSelection.achRecordFlavor)) {
				Vector<ACHRecord> text = (Vector<ACHRecord>) (t
						.getTransferData(ACHSelection.achRecordFlavor));
				return text;
			}
		} catch (UnsupportedFlavorException e) {
		} catch (IOException e) {
		}
		return null;
	}

	public Integer[] getPositions(final int index) {
        return positions.get(index);
    }

    public ACHRecord getRow(int i) {
        return (ACHRecord) jListAchDataAchRecords.getModel()
        		.getElementAt(i);
    }

    public int[] getSelectedRows() {
        return jListAchDataAchRecords.getSelectedIndices();
    }
    
    /**
	 * This method is called from within the constructor to initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is always
	 * regenerated by the Form Editor.
	 */
	// <editor-fold defaultstate="collapsed" desc=" Generated Code
	// <editor-fold defaultstate="collapsed" desc=" Generated Code
	// <editor-fold defaultstate="collapsed" desc=" Generated Code
	// <editor-fold defaultstate="collapsed" desc=" Generated Code
	// <editor-fold defaultstate="collapsed" desc=" Generated Code
	// <editor-fold defaultstate="collapsed" desc=" Generated Code
	// <editor-fold defaultstate="collapsed" desc=" Generated Code
	// <editor-fold defaultstate="collapsed" desc=" Generated Code
	// <editor-fold defaultstate="collapsed" desc=" Generated Code
	// <editor-fold defaultstate="collapsed" desc=" Generated Code
	// <editor-fold defaultstate="collapsed" desc=" Generated Code
	// <editor-fold defaultstate="collapsed" desc=" Generated Code
	// <editor-fold defaultstate="collapsed" desc=" Generated Code
	// ">//GEN-BEGIN:initComponents
	private void initComponents() {
		jPopupMenuEntry = new javax.swing.JPopupMenu();
		jMenuItemPopupEntryAdd = new javax.swing.JMenuItem();
		jMenuItemPopupEntryAddAddenda = new javax.swing.JMenuItem();
		jMenuItemPopupEntryDelete = new javax.swing.JMenuItem();
		jMenuItemPopupEntryEditEntry = new javax.swing.JMenuItem();
		jMenuItemPopupEntryCopy = new javax.swing.JMenuItem();
		jMenuItemPopupEntryPaste = new javax.swing.JMenuItem();
		jPopupMenuBatch = new javax.swing.JPopupMenu();
		jMenuItemPopupBatchAdd = new javax.swing.JMenuItem();
		jMenuItemPopupBatchAddEntry = new javax.swing.JMenuItem();
		jMenuItemPopupBatchDeleteBatch = new javax.swing.JMenuItem();
		jMenuItemPopupBatchEditBatch = new javax.swing.JMenuItem();
		jMenuItemPopupBatchCopy = new javax.swing.JMenuItem();
		jMenuItemPopupBatchPaste = new javax.swing.JMenuItem();
		jPopupMenuAddenda = new javax.swing.JPopupMenu();
		jMenuItemPopupEntryAddendaAdd = new javax.swing.JMenuItem();
		jMenuItemPopupAddendaDelete = new javax.swing.JMenuItem();
		jMenuItemPopupAddendaCopy = new javax.swing.JMenuItem();
		jMenuItemPopupAddendaPaste = new javax.swing.JMenuItem();
		jPopupMenuFile = new javax.swing.JPopupMenu();
		jMenuItemPopupFileAddBatch = new javax.swing.JMenuItem();
		jMenuItemPopupFileEdit = new javax.swing.JMenuItem();
		jPopupMenuMultipleSelection = new javax.swing.JPopupMenu();
		jMenuItemMulitpleDelete = new javax.swing.JMenuItem();
		jMenuItemPopupMultipleCopy = new javax.swing.JMenuItem();
		jMenuItemPopupPasteMultiple = new javax.swing.JMenuItem();
		jPanel1 = new javax.swing.JPanel();
		jLabelAchInfoFileNameText = new javax.swing.JLabel();
		jLabelAchInfoImmDestText = new javax.swing.JLabel();
		jLabelAchInfoImmOriginText = new javax.swing.JLabel();
		jLabelAchInfoFileName = new javax.swing.JLabel();
		jLabelAchInfoImmDest = new javax.swing.JLabel();
		jLabelAchInfoImmOrigin = new javax.swing.JLabel();
		jLabelAchInfoFileCreation = new javax.swing.JLabel();
		jLabelAchInfoFileCreationText = new javax.swing.JLabel();
		jLabelAchInfoBatchCountText = new javax.swing.JLabel();
		jLabelAchInfoBatchCount = new javax.swing.JLabel();
		jLabelAchInfoEntryCountText = new javax.swing.JLabel();
		jLabelAchInfoEntryCount = new javax.swing.JLabel();
		jLabelAchInfoTotalDebitText = new javax.swing.JLabel();
		jLabelAchInfoTotalCreditText = new javax.swing.JLabel();
		jLabelAchInfoTotalDebit = new javax.swing.JLabel();
		jLabelAchInfoTotalCredit = new javax.swing.JLabel();
		jScrollPane1 = new javax.swing.JScrollPane();
		jListAchDataAchRecords = new javax.swing.JList();
		jLabel1 = new javax.swing.JLabel();
		jLabel2 = new javax.swing.JLabel();
		jMenuBar = new javax.swing.JMenuBar();
		jMenuFile = new javax.swing.JMenu();
		jMenuItemFileNew = new javax.swing.JMenuItem();
		jMenuItemFileOpen = new javax.swing.JMenuItem();
		jCheckBoxMenuFedFile = new javax.swing.JCheckBoxMenuItem();
		jMenuItemFileSave = new javax.swing.JMenuItem();
		jMenuItemFileSaveAs = new javax.swing.JMenuItem();
		jSeparatorMenuFile = new javax.swing.JSeparator();
		jMenuItemFileExit = new javax.swing.JMenuItem();
		jMenuTools = new javax.swing.JMenu();
		jMenuItemToolsValidate = new javax.swing.JMenuItem();
		jMenuItemToolsRecalculate = new javax.swing.JMenuItem();
		jMenuItemToolsReverse = new javax.swing.JMenuItem();

		jMenuItemPopupEntryAdd.setText("Add Entry Record");


		jPopupMenuEntry.add(jMenuItemPopupEntryAdd);

		jMenuItemPopupEntryAddAddenda.setText("Add Addenda");


		jPopupMenuEntry.add(jMenuItemPopupEntryAddAddenda);

		jMenuItemPopupEntryDelete.setText("Delete Entry");

		jPopupMenuEntry.add(jMenuItemPopupEntryDelete);

		jMenuItemPopupEntryEditEntry.setText("Edit Entry");


		jPopupMenuEntry.add(jMenuItemPopupEntryEditEntry);

		jMenuItemPopupEntryCopy.setText("Copy");


		jPopupMenuEntry.add(jMenuItemPopupEntryCopy);

		jMenuItemPopupEntryPaste.setText("Paste");


		jPopupMenuEntry.add(jMenuItemPopupEntryPaste);

		jMenuItemPopupBatchAdd.setText("Add new batch");
		

		jPopupMenuBatch.add(jMenuItemPopupBatchAdd);

		jMenuItemPopupBatchAddEntry.setText("Add new entry");


		jPopupMenuBatch.add(jMenuItemPopupBatchAddEntry);

		jMenuItemPopupBatchDeleteBatch.setText("Delete Batch");

		jPopupMenuBatch.add(jMenuItemPopupBatchDeleteBatch);

		jMenuItemPopupBatchEditBatch.setText("Edit Batch");


		jPopupMenuBatch.add(jMenuItemPopupBatchEditBatch);

		jMenuItemPopupBatchCopy.setText("Copy");


		jPopupMenuBatch.add(jMenuItemPopupBatchCopy);

		jMenuItemPopupBatchPaste.setText("Paste");

		jPopupMenuBatch.add(jMenuItemPopupBatchPaste);

		jMenuItemPopupEntryAddendaAdd.setText("Add Addenda");


		jPopupMenuAddenda.add(jMenuItemPopupEntryAddendaAdd);

		jMenuItemPopupAddendaDelete.setText("Delete Addenda");

		jPopupMenuAddenda.add(jMenuItemPopupAddendaDelete);

		jMenuItemPopupAddendaCopy.setText("Copy");

		jPopupMenuAddenda.add(jMenuItemPopupAddendaCopy);

		jMenuItemPopupAddendaPaste.setText("Paste");

		jPopupMenuAddenda.add(jMenuItemPopupAddendaPaste);

		jMenuItemPopupFileAddBatch.setText("Add new batch");

		jPopupMenuFile.add(jMenuItemPopupFileAddBatch);

		jMenuItemPopupFileEdit.setText("Edit File");

		jPopupMenuFile.add(jMenuItemPopupFileEdit);

		jMenuItemMulitpleDelete.setText("Delete");

		jPopupMenuMultipleSelection.add(jMenuItemMulitpleDelete);

		jMenuItemPopupMultipleCopy.setText("Copy");

		jPopupMenuMultipleSelection.add(jMenuItemPopupMultipleCopy);

		jMenuItemPopupPasteMultiple.setText("Paste");


		jPopupMenuMultipleSelection.add(jMenuItemPopupPasteMultiple);

		setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
		setTitle("ACH Editor");
		setName("ACHMainFrame");
		jPanel1.setBorder(javax.swing.BorderFactory
				.createTitledBorder("ACH Information"));
		jLabelAchInfoFileNameText.setText("File Name:");

		jLabelAchInfoImmDestText.setText("Destination:");

		jLabelAchInfoImmOriginText.setText("Origin:");

		jLabelAchInfoFileName.setText("File Name");

		jLabelAchInfoImmDest
				.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
		jLabelAchInfoImmDest.setText("1234567890");

		jLabelAchInfoImmOrigin
				.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
		jLabelAchInfoImmOrigin.setText("1234567890");

		jLabelAchInfoFileCreation.setText("File Creation");

		jLabelAchInfoFileCreationText.setText("File Creation:");

		jLabelAchInfoBatchCountText.setText("Batch Count:");

		jLabelAchInfoBatchCount
				.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
		jLabelAchInfoBatchCount.setText("12,345");

		jLabelAchInfoEntryCountText.setText("Entry Count:");

		jLabelAchInfoEntryCount
				.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
		jLabelAchInfoEntryCount.setText("12,345");

		jLabelAchInfoTotalDebitText.setText("Total Debit:");

		jLabelAchInfoTotalCreditText.setText("Total Credit:");

		jLabelAchInfoTotalDebit
				.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
		jLabelAchInfoTotalDebit.setText("123,456,789.00");

		jLabelAchInfoTotalCredit
				.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
		jLabelAchInfoTotalCredit.setText("123,456,789.00");
		jLabelAchInfoTotalCredit.setToolTipText(String.valueOf(jLabelAchInfoTotalCredit.getWidth()));

		javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(
				jPanel1);
		jPanel1.setLayout(jPanel1Layout);
		jPanel1Layout
				.setHorizontalGroup(jPanel1Layout
						.createParallelGroup(
								javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(
								jPanel1Layout
										.createSequentialGroup()
										.addContainerGap()
										.addGroup(
												jPanel1Layout
														.createParallelGroup(
																javax.swing.GroupLayout.Alignment.LEADING)
														.addGroup(
																jPanel1Layout
																		.createSequentialGroup()
																		.addGroup(
																				jPanel1Layout
																						.createParallelGroup(
																								javax.swing.GroupLayout.Alignment.TRAILING,
																								false)
																						.addComponent(
																								jLabelAchInfoBatchCountText,
																								javax.swing.GroupLayout.Alignment.LEADING,
																								javax.swing.GroupLayout.DEFAULT_SIZE,
																								javax.swing.GroupLayout.DEFAULT_SIZE,
																								Short.MAX_VALUE)
																						.addComponent(
																								jLabelAchInfoFileCreationText,
																								javax.swing.GroupLayout.Alignment.LEADING,
																								javax.swing.GroupLayout.DEFAULT_SIZE,
																								javax.swing.GroupLayout.DEFAULT_SIZE,
																								Short.MAX_VALUE)
																						.addComponent(
																								jLabelAchInfoFileNameText,
																								javax.swing.GroupLayout.Alignment.LEADING,
																								javax.swing.GroupLayout.DEFAULT_SIZE,
																								90,
																								Short.MAX_VALUE))
																		.addPreferredGap(
																				javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																		.addGroup(
																				jPanel1Layout
																						.createParallelGroup(
																								javax.swing.GroupLayout.Alignment.LEADING)
																						.addComponent(
																								jLabelAchInfoFileName,
																								javax.swing.GroupLayout.PREFERRED_SIZE,
																								549,
																								javax.swing.GroupLayout.PREFERRED_SIZE)
																						.addComponent(
																								jLabelAchInfoFileCreation,
																								javax.swing.GroupLayout.PREFERRED_SIZE,
																								157,
																								javax.swing.GroupLayout.PREFERRED_SIZE)
																						.addGroup(
																								jPanel1Layout
																										.createSequentialGroup()
																										.addComponent(
																												jLabelAchInfoBatchCount,
																												javax.swing.GroupLayout.PREFERRED_SIZE,
																												114,
																												javax.swing.GroupLayout.PREFERRED_SIZE)
																										.addGap(
																												40,
																												40,
																												40)
																										.addGroup(
																												jPanel1Layout
																														.createParallelGroup(
																																javax.swing.GroupLayout.Alignment.TRAILING,
																																false)
																														.addComponent(
																																jLabelAchInfoTotalDebitText,
																																javax.swing.GroupLayout.Alignment.LEADING,
																																javax.swing.GroupLayout.DEFAULT_SIZE,
																																javax.swing.GroupLayout.DEFAULT_SIZE,
																																Short.MAX_VALUE)
																														.addComponent(
																																jLabelAchInfoEntryCountText,
																																javax.swing.GroupLayout.Alignment.LEADING,
																																javax.swing.GroupLayout.DEFAULT_SIZE,
																																78,
																																Short.MAX_VALUE))
																										.addPreferredGap(
																												javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																										.addGroup(
																												jPanel1Layout
																														.createParallelGroup(
																																javax.swing.GroupLayout.Alignment.LEADING)
																														.addComponent(
																																jLabelAchInfoTotalDebit,
																																javax.swing.GroupLayout.PREFERRED_SIZE,
																																javax.swing.GroupLayout.PREFERRED_SIZE,
																																javax.swing.GroupLayout.PREFERRED_SIZE)
																														.addComponent(
																																jLabelAchInfoEntryCount,
																																javax.swing.GroupLayout.PREFERRED_SIZE,
																																javax.swing.GroupLayout.PREFERRED_SIZE,
																																javax.swing.GroupLayout.PREFERRED_SIZE))))
																		.addContainerGap())
														.addGroup(
																jPanel1Layout
																		.createSequentialGroup()
																		.addComponent(
																				jLabelAchInfoTotalCreditText,
																				javax.swing.GroupLayout.DEFAULT_SIZE,
																				90,
																				Short.MAX_VALUE)
																		.addPreferredGap(
																				javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																		.addComponent(
																				jLabelAchInfoTotalCredit,
																				javax.swing.GroupLayout.PREFERRED_SIZE,
																				javax.swing.GroupLayout.PREFERRED_SIZE,
																				javax.swing.GroupLayout.PREFERRED_SIZE)
																		.addGap(
																				464,
																				464,
																				464))
														.addGroup(
																javax.swing.GroupLayout.Alignment.TRAILING,
																jPanel1Layout
																		.createSequentialGroup()
																		.addGroup(
																				jPanel1Layout
																						.createParallelGroup(
																								javax.swing.GroupLayout.Alignment.TRAILING)
																						.addGroup(
																								javax.swing.GroupLayout.Alignment.LEADING,
																								jPanel1Layout
																										.createSequentialGroup()
																										.addComponent(
																												jLabelAchInfoImmDestText,
																												javax.swing.GroupLayout.DEFAULT_SIZE,
																												90,
																												Short.MAX_VALUE)
																										.addPreferredGap(
																												javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																										.addComponent(
																												jLabelAchInfoImmDest,
																												javax.swing.GroupLayout.PREFERRED_SIZE,
																												343,
																												javax.swing.GroupLayout.PREFERRED_SIZE))
																						.addGroup(
																								jPanel1Layout
																										.createSequentialGroup()
																										.addComponent(
																												jLabelAchInfoImmOriginText,
																												javax.swing.GroupLayout.DEFAULT_SIZE,
																												90,
																												Short.MAX_VALUE)
																										.addPreferredGap(
																												javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																										.addComponent(
																												jLabelAchInfoImmOrigin,
																												javax.swing.GroupLayout.PREFERRED_SIZE,
																												343,
																												javax.swing.GroupLayout.PREFERRED_SIZE)))
																		.addGap(
																				216,
																				216,
																				216)))));
		jPanel1Layout
				.setVerticalGroup(jPanel1Layout
						.createParallelGroup(
								javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(
								jPanel1Layout
										.createSequentialGroup()
										.addGroup(
												jPanel1Layout
														.createParallelGroup(
																javax.swing.GroupLayout.Alignment.BASELINE)
														.addComponent(
																jLabelAchInfoFileNameText)
														.addComponent(
																jLabelAchInfoFileName))
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addGroup(
												jPanel1Layout
														.createParallelGroup(
																javax.swing.GroupLayout.Alignment.BASELINE)
														.addComponent(
																jLabelAchInfoFileCreationText,
																javax.swing.GroupLayout.PREFERRED_SIZE,
																13,
																javax.swing.GroupLayout.PREFERRED_SIZE)
														.addComponent(
																jLabelAchInfoFileCreation))
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addGroup(
												jPanel1Layout
														.createParallelGroup(
																javax.swing.GroupLayout.Alignment.BASELINE)
														.addComponent(
																jLabelAchInfoBatchCountText)
														.addComponent(
																jLabelAchInfoBatchCount)
														.addComponent(
																jLabelAchInfoEntryCountText)
														.addComponent(
																jLabelAchInfoEntryCount))
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addGroup(
												jPanel1Layout
														.createParallelGroup(
																javax.swing.GroupLayout.Alignment.BASELINE)
														.addComponent(
																jLabelAchInfoTotalCreditText)
														.addComponent(
																jLabelAchInfoTotalCredit)
														.addComponent(
																jLabelAchInfoTotalDebitText)
														.addComponent(
																jLabelAchInfoTotalDebit))
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addGroup(
												jPanel1Layout
														.createParallelGroup(
																javax.swing.GroupLayout.Alignment.BASELINE)
														.addComponent(
																jLabelAchInfoImmDestText)
														.addComponent(
																jLabelAchInfoImmDest))
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addGroup(
												jPanel1Layout
														.createParallelGroup(
																javax.swing.GroupLayout.Alignment.BASELINE)
														.addComponent(
																jLabelAchInfoImmOriginText)
														.addComponent(
																jLabelAchInfoImmOrigin))
										.addContainerGap(
												javax.swing.GroupLayout.DEFAULT_SIZE,
												Short.MAX_VALUE)));

		jScrollPane1.setBorder(javax.swing.BorderFactory
				.createTitledBorder("ACH Data"));
		jListAchDataAchRecords.setFont(new java.awt.Font("Courier New", 0, 12));


		jScrollPane1.setViewportView(jListAchDataAchRecords);

		jLabel1.setFont(new java.awt.Font("Courier New", 0, 12));
		jLabel1
				.setText("         1         2         3         4         5         6         7         8         9 ");

		jLabel2.setFont(new java.awt.Font("Courier New", 0, 12));
		jLabel2
				.setText("1234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234");

		jMenuFile.setText("File");
		jMenuItemFileNew.setText("New");
		jMenuItemFileNew.setToolTipText("Create new ACH data");
	

		jMenuFile.add(jMenuItemFileNew);

		jMenuItemFileOpen.setText("Open...");
		jMenuItemFileOpen.setToolTipText("Open new file");

		jMenuFile.add(jMenuItemFileOpen);

		jCheckBoxMenuFedFile.setText("Output as Fed File");
		jMenuFile.add(jCheckBoxMenuFedFile);

		jMenuItemFileSave.setText("Save");
		jMenuItemFileSave
				.setToolTipText("Saves existing data overwriting current file");

		jMenuFile.add(jMenuItemFileSave);

		jMenuItemFileSaveAs.setText("Save As...");
		jMenuItemFileSaveAs.setToolTipText("Save into new file");

		jMenuFile.add(jMenuItemFileSaveAs);

		jMenuFile.add(jSeparatorMenuFile);

		jMenuItemFileExit.setText("Exit");
		jMenuItemFileExit.setToolTipText("Exit program");
		

		jMenuFile.add(jMenuItemFileExit);

		jMenuBar.add(jMenuFile);

		jMenuTools.setText("Tools");
		jMenuTools.setToolTipText("");
		jMenuItemToolsValidate.setText("Validate");
		jMenuItemToolsValidate
				.setToolTipText("Check the validity of the ACH file");

		jMenuTools.add(jMenuItemToolsValidate);

		jMenuItemToolsRecalculate.setText("Recalculate");
		jMenuItemToolsRecalculate
				.setToolTipText("Recalculate the file and batch control totals");
		ACHEditorView view = this;


		jMenuTools.add(jMenuItemToolsRecalculate);

		jMenuItemToolsReverse.setText("Reverse");
		jMenuItemToolsReverse
				.setToolTipText("Reverse this ACH file and recalculate totals");

		jMenuTools.add(jMenuItemToolsReverse);

		jMenuBar.add(jMenuTools);

		setJMenuBar(jMenuBar);

		javax.swing.GroupLayout layout = new javax.swing.GroupLayout(
				getContentPane());
		getContentPane().setLayout(layout);
		layout
				.setHorizontalGroup(layout
						.createParallelGroup(
								javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(
								layout
										.createSequentialGroup()
										.addContainerGap()
										.addGroup(
												layout
														.createParallelGroup(
																javax.swing.GroupLayout.Alignment.LEADING)
														.addComponent(
																jScrollPane1,
																javax.swing.GroupLayout.DEFAULT_SIZE,
																696,
																Short.MAX_VALUE)
														.addGroup(
																layout
																		.createSequentialGroup()
																		.addComponent(
																				jLabel1,
																				javax.swing.GroupLayout.DEFAULT_SIZE,
																				686,
																				Short.MAX_VALUE)
																		.addContainerGap())
														.addGroup(
																layout
																		.createSequentialGroup()
																		.addComponent(
																				jLabel2,
																				javax.swing.GroupLayout.PREFERRED_SIZE,
																				667,
																				javax.swing.GroupLayout.PREFERRED_SIZE)
																		.addGap(
																				29,
																				29,
																				29))
														.addGroup(
																layout
																		.createSequentialGroup()
																		.addComponent(
																				jPanel1,
																				javax.swing.GroupLayout.PREFERRED_SIZE,
																				javax.swing.GroupLayout.DEFAULT_SIZE,
																				javax.swing.GroupLayout.PREFERRED_SIZE)
																		.addContainerGap()))));
		layout
				.setVerticalGroup(layout
						.createParallelGroup(
								javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(
								layout
										.createSequentialGroup()
										.addComponent(
												jPanel1,
												javax.swing.GroupLayout.PREFERRED_SIZE,
												javax.swing.GroupLayout.DEFAULT_SIZE,
												javax.swing.GroupLayout.PREFERRED_SIZE)
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addComponent(
												jLabel1,
												javax.swing.GroupLayout.PREFERRED_SIZE,
												14,
												javax.swing.GroupLayout.PREFERRED_SIZE)
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addComponent(jLabel2)
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addComponent(
												jScrollPane1,
												javax.swing.GroupLayout.DEFAULT_SIZE,
												348, Short.MAX_VALUE)));
		pack();
	}// </editor-fold>//GEN-END:initComponents

    

    
    public void loadAchDataRecords() {
	    final ACHDocument achFile = model.getAchFile();
	    List<ACHRecord> records = new ArrayList<>();
		positions = new Vector<Integer[]>(10, 10);
		records.add(achFile.getFileHeader());
		positions.add(new Integer[0]);
		Vector<ACHBatch> achBatches = achFile.getBatches();
		for (int i = 0; i < achBatches.size(); i++) {
			records.add(achBatches.get(i).getBatchHeader());
			positions.add(new Integer[] { i });
			Vector<ACHEntry> achEntries = achBatches.get(i).getEntryRecs();
			for (int j = 0; j < achEntries.size(); j++) {
				records.add(achEntries.get(j)
                        .getEntryDetail());
				positions.add(new Integer[] { i, j });
				Vector<ACHRecordAddenda> achAddendas = achEntries.get(j)
						.getAddendaRecs();
				for (int k = 0; k < achAddendas.size(); k++) {
					records.add(achAddendas.get(k));
					positions.add(new Integer[] { i, j, k });
				}
			}
			records.add(achBatches.get(i).getBatchControl());
			positions.add(new Integer[] { i });
		}
		records.add(achFile.getFileControl());
		for (ACHRecord rec: records) {
		    addJListAchDataAchRecordsItem(rec);
		}
		positions.add(new Integer[0]);
		jMenuItemToolsRecalculate.setEnabled(true);
		jMenuItemToolsValidate.setEnabled(true);
	}
    private void loadAchInformation() {
	    final ACHDocument achFile = model.getAchFile();
		jLabelAchInfoFileCreation.setText(achFile.getFileHeader()
				.getFileCreationDate()
				+ " " + achFile.getFileHeader().getFileCreationTime());
		jLabelAchInfoBatchCount.setText(achFile.getFileControl()
				.getBatchCount());
		jLabelAchInfoEntryCount.setText(achFile.getFileControl()
				.getEntryAddendaCount());
		jLabelAchInfoTotalDebit.setText(achFile.getFileControl()
				.getTotDebitDollarAmt());
		jLabelAchInfoTotalCredit.setText(achFile.getFileControl()
				.getTotCreditDollarAmt());
		jLabelAchInfoImmDest.setText(achFile.getFileHeader()
				.getImmediateDestination()
				+ " " + achFile.getFileHeader().getImmediateDestinationName());
		jLabelAchInfoImmOrigin.setText(achFile.getFileHeader()
				.getImmediateOrigin()
				+ " " + achFile.getFileHeader().getImmediateOriginName());
		jCheckBoxMenuFedFile.setSelected(achFile.isFedFile());

	}

    /* (non-Javadoc)
     * @see com.ach.editor.model.ModelListener#onSetAchFile()
     */
    @Override
    public void onSetAchFile() {
        ACHDocument achFile = model.getAchFile();
        jCheckBoxMenuFedFile.setSelected(achFile.isFedFile());
        loadAchInformation();
        clearJListAchDataAchRecords();
        loadAchDataRecords();
    }

    @Override
    public void onSetFileDirty() {
        File outputFile = model.getOutputFile();
        final String prefix = model.isAchFileDirty() ? "*" : "";
        final String name = outputFile != null ? outputFile.getAbsolutePath() : "--no file--";
        setTitle(prefix + name);
    }

    @Override
    public void onSetOutputFile() {
        final File outputFile = model.getOutputFile();
        final String title = outputFile.getAbsolutePath();
        jLabelAchInfoFileName.setText(title);
        setTitle(title);
    }

    @Override
    public void onSetSelectedRow() {
        int index = model.getSelectedRow();
        jListAchDataAchRecords.setSelectedIndex(index);
        jListAchDataAchRecords.ensureIndexIsVisible(index);
    }

    public void putRow(int selectRow, final ACHRecord achRecord) {
	    ((DefaultListModel) jListAchDataAchRecords.getModel()).setElementAt(achRecord, selectRow);
	}
    
    public void registerListener(ACHEditorViewListener viewListener) {
        jMenuItemFileOpen.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                viewListener.onFileOpen();
            }
        });
        jMenuItemFileNew.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                viewListener.onFileNew();
            }
        });
        jMenuItemPopupEntryAdd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                viewListener.addAchEntryDetail();
            }
        });
        jMenuItemPopupEntryAddAddenda.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                viewListener.addAchAddenda();
            }
        });
        jMenuItemPopupEntryEditEntry.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                viewListener.onItemPopupEntryEditEntry();
            }
        });
        jMenuItemPopupEntryDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                viewListener.onDeleteAchEntryDetail();
            }
        });
        jMenuItemPopupEntryPaste.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                viewListener.onItemPopupEntryPaste();
            }
        });
        jMenuItemPopupEntryCopy.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                viewListener.onItemPopupEntryCopy();
            }
        });
        jMenuItemPopupBatchAdd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                viewListener.addAchBatch();
            }
        });
        jMenuItemPopupBatchAddEntry.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                viewListener.addAchEntryDetail();
            }
        });
        jMenuItemPopupBatchDeleteBatch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                viewListener.onDeleteAchBatch();
            }
        });
        jMenuItemPopupBatchEditBatch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                viewListener.onItemPopupBatchEditBatch();
            }
        });
        jMenuItemPopupPasteMultiple.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                viewListener.onItemPopupPasteMultiple();
            }
        });
        jMenuItemPopupBatchCopy.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                viewListener.onItemPopupBatchCopy();
            }
        });
        jMenuItemPopupBatchPaste
        .addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                viewListener.onItemPopupBatchPaste();
            }
        });
        jMenuItemPopupEntryAddendaAdd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                viewListener.addAchAddenda();
            }
        });
        jMenuItemPopupAddendaDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                viewListener.onItemPopupAddendaDelete();
            }
        });
        jMenuItemPopupAddendaCopy
        .addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                viewListener.onItemPopupAddendaCopy();
            }
        });
        jMenuItemPopupAddendaPaste.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                viewListener.onItemPopupAddendaPaste();
            }
        });
        jMenuItemPopupFileAddBatch
        .addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                viewListener.addAchBatch();
            }
        });
        jMenuItemPopupFileEdit
        .addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                viewListener.onItemPopupFileEdit();
            }
        });
        jMenuItemMulitpleDelete
        .addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                viewListener.onItemMultipleDelete();
            }
        });
        jMenuItemPopupMultipleCopy
        .addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                viewListener.onItemPopupMultipleCopy();
            }
        });
        jMenuItemFileSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                viewListener.onFileSave();
            }
        });
        jMenuItemFileSaveAs
        .addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                viewListener.onFileSaveAs();
            }
        });
        jMenuItemFileExit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                viewListener.onExitProgram();
            }
        });
        jMenuItemToolsValidate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                viewListener.onItemToolsValidate();
            }
        });
        jMenuItemToolsRecalculate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                viewListener.onItemToolsRecalculate();
            }
        });
        jMenuItemToolsReverse.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                viewListener.onItemToolsReverse();
            }
        });
        ACHEditorView that = this;
        jListAchDataAchRecords.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                int clickCount = evt.getClickCount();
                int button = evt.getButton();
                mouseClick = evt.getPoint();
                viewListener.onListClick(that.clickedIndex(), clickCount, button);
            }
        });
        jCheckBoxMenuFedFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                viewListener.onIsFedFile();
            }
        });
        addWindowListener(new WindowListener() {
            public void windowActivated(WindowEvent evt) {
            }

            public void windowClosed(WindowEvent evt) {
            }

            public void windowClosing(WindowEvent evt) {
                viewListener.onExitProgram();
            }

            public void windowDeactivated(WindowEvent evt) {
            }

            public void windowDeiconified(WindowEvent evt) {
            }

            public void windowIconified(WindowEvent evt) {
            }

            public void windowOpened(WindowEvent evt) {
            }
        });
    }

    public void setCursorDefault() {
        Cursor currentCursor = getCursor();
        final int cursorType = currentCursor.getType();
        if (cursorType != Cursor.DEFAULT_CURSOR) {
            setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        }
    }

    public void setCursorWait() {
        Cursor currentCursor = getCursor();
        if (currentCursor.getType() == Cursor.DEFAULT_CURSOR) {
            setCursor(new Cursor(Cursor.WAIT_CURSOR));
        }
    }

    /**
     * @param dialog
     */
    public void showDialog(JPopupMenu dialog) {
        dialog.show(jListAchDataAchRecords, mouseClick.x, mouseClick.y);
    }
    /**
     * @param title
     * @param message
     */
    public void showError(String title, String message) {
        JOptionPane.showMessageDialog(this,
                message,
                title,
                JOptionPane.ERROR_MESSAGE);

    }

    public void showMessage(final String msg) {
        JOptionPane.showMessageDialog(this, msg);
    }

    /**
     * 
     */
    public void exit() {
        dispose();
        System.exit(0);
    }

}
