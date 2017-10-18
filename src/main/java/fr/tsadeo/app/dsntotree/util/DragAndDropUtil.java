package fr.tsadeo.app.dsntotree.util;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragSource;
import java.awt.dnd.DragSourceAdapter;
import java.awt.dnd.DragSourceContext;
import java.awt.dnd.DragSourceDragEvent;
import java.awt.dnd.DragSourceDropEvent;
import java.awt.dnd.DragSourceEvent;
import java.awt.dnd.DropTargetAdapter;
import java.awt.dnd.DropTargetContext;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.TransferHandler;

import fr.tsadeo.app.dsntotree.gui.IGuiConstants;
import fr.tsadeo.app.dsntotree.gui.IMainActionListener;
import fr.tsadeo.app.dsntotree.gui.MyPanelBloc.PanelChild;
import fr.tsadeo.app.dsntotree.model.ItemBloc;

public class DragAndDropUtil {

    private static final Logger LOG = Logger.getLogger(DragAndDropUtil.class.getName());
    
    private static Cursor dragCursor = Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR);

    private static DragAndDropUtil instance;

    public static DragAndDropUtil get() {
        if (instance == null) {
            instance = new DragAndDropUtil();
        }
        return instance;
    }

    private DataFlavor itemNodeDataFlavor;

    public DataFlavor getItemBlocDataFlavor() {

        if (this.itemNodeDataFlavor == null) {
            try {
                this.itemNodeDataFlavor = new DataFlavor(
                        DataFlavor.javaJVMLocalObjectMimeType + ";class=fr.tsadeo.app.dsntotree.model.ItemBloc");
            } catch (ClassNotFoundException e) {
                LOG.severe("ItemBlocDataFlavor.get(): " + e.toString());
            }
        }
        return itemNodeDataFlavor;
    }


    public interface ITreeDndController {

        public boolean canPerformAction(ItemBloc blocToDrop, Point point);
        
        public ItemBloc getTarget(Point point);
        
        public void onItemBlocDragStarted();

        public void onItemBlocDropEnded();


    }

//    public void createDefaultDragGestureRecognizer(MySimpleTree tree, ItemBlocListener itemBlocListener) {
//      DragSource dragSource = DragSource.getDefaultDragSource();
//      dragSource.createDefaultDragGestureRecognizer(tree, DnDConstants.ACTION_COPY,
//              new TreeDragSource(tree, itemBlocListener));    	
//    }
    public void createDefaultDragGestureRecognizer(final PanelChild panelChild) {
    	
    	DragSource dragSource = DragSource.getDefaultDragSource();
        dragSource.createDefaultDragGestureRecognizer(panelChild, DnDConstants.ACTION_COPY,
                this.getPanelChildDragSourceListener());
        
        panelChild.addMouseListener(this.getPanelChildMouseListener());
    }
    
    private MouseListener panelChildMouseListener;
    private MouseListener getPanelChildMouseListener() {
    	if (this.panelChildMouseListener == null) {
    		this.panelChildMouseListener =new MouseAdapter() {
            	

    			@Override
    			public void mouseEntered(MouseEvent e) {
    				
    				if (e.getSource() instanceof PanelChild) {
    					PanelChild src = (PanelChild)e.getSource();
    					src.setBackground(IGuiConstants.DRAG_START_COLOR);
    				}
    			}

    			@Override
    			public void mouseExited(MouseEvent e) {
    				if (e.getSource() instanceof PanelChild) {
    					PanelChild src = (PanelChild)e.getSource();
    					src.setBackground(null);
    				}
    			}
            	
    		};
    	}
    	return this.panelChildMouseListener;
    }
    
   
    private PanelChildDragSourceListener panelChildDragSourceListener;
    private PanelChildDragSourceListener getPanelChildDragSourceListener() {
    	if (this.panelChildDragSourceListener == null) {
    		this.panelChildDragSourceListener = new PanelChildDragSourceListener();
    	}
    	return this.panelChildDragSourceListener;
    }
    private static class PanelChildDragSourceListener extends AbstractItemBlocDragSourceListener {

        private  PanelChild panelChild;
        private  Color color;

        private PanelChildDragSourceListener() {
        }
        //------------------------- overriding AbstractItemBlocDragSourceListener
        @Override
        protected ItemBloc getItemBloc(DragGestureEvent evt) {
        	
        	this.panelChild = this.getPanelChild(evt);
            this.color = this.panelChild == null?null:this.panelChild.getBackground();
            return  this.panelChild == null?null: this.panelChild.getItemBloc();
        }
        @Override
        protected  void startingDrag() {
        	if (this.panelChild != null) {
        	  this.panelChild.setBackground(DRAG_START_COLOR);
        	}
        }

		@Override
		protected JPanel getPanelForImage() {
			return this.panelChild;
		}
		//------------------------------------ private methods
        private PanelChild getPanelChild(DragGestureEvent evt) {
        	if (evt.getComponent() instanceof PanelChild) {
        		return (PanelChild) evt.getComponent();
        	}
        	return null;
        }
        //------------------- overriding DragSourceListener
        @Override
        public void dragDropEnd(DragSourceDropEvent evt) {
        	if (this.panelChild != null && this.color != null) {
        		this.panelChild.setBackground(this.color);
        	}
        	super.dragDropEnd(evt);
        }

    }

    private static abstract class AbstractItemBlocDragSourceListener extends DragSourceAdapter 
    implements DragGestureListener, IGuiConstants

    {

        protected abstract ItemBloc getItemBloc(DragGestureEvent evt);
        protected abstract JPanel getPanelForImage();

        // --------------------------- overriding DragSourceAdapter
        
        @Override
        public void dragEnter(DragSourceDragEvent evt) {
            DragSourceContext ctx = evt.getDragSourceContext();
            ctx.setCursor(dragCursor);
            LOG.config("dragEnter");
        }

		@Override
        public void dragExit(DragSourceEvent evt) {
            DragSourceContext ctx = evt.getDragSourceContext();
            ctx.setCursor(DragSource.DefaultCopyNoDrop);
            LOG.config("dragExit");
        }

        public void dragDropEnd(DragSourceDropEvent evt) {
            DragSourceContext ctx = evt.getDragSourceContext();
            ctx.setCursor(dragCursor);
            LOG.config("dragDropEnd fired");
        }
        
        protected abstract void startingDrag();

        // ------------------------- implementing DragGestureListener
        @Override
        public void dragGestureRecognized(DragGestureEvent evt) {
        	
            ItemBloc itemBloc = this.getItemBloc(evt);
            if (itemBloc != null) {
                LOG.fine("startDrag() bloc " + itemBloc.toString());
                this.startingDrag();
                
                JPanel panel = this.getPanelForImage();
                if (panel != null) {
                	BufferedImage dragImage = new BufferedImage(panel.getWidth(), panel.getHeight(), BufferedImage.TYPE_INT_BGR);
                	Graphics g = dragImage.getGraphics();
                	panel.paint(g);
                	evt.startDrag(dragCursor, dragImage, new Point(), new ItemBlocTransferable(itemBloc), this);
                }
                
                
            }
        }

    }

//    private static class TreeDragSource extends AbstractItemBlocDragSourceListener {
//
//        private MySimpleTree tree;
//
//        private TreeDragSource(MySimpleTree tree, ItemBlocListener itemBlocListener) {
//            super(itemBlocListener);
//            this.tree = tree;
//        }
//
//        @Override
//        protected ItemBloc getItemBloc() {
//            return tree.getItemBlocFromSelection();
//        }
//
//		@Override
//		protected void startDrag() {
//			// TODO Auto-generated method stub
//			
//		}
//
//    }

    /**
     * Transferable encapsulant un ItemBloc
     *
     */
    private static class ItemBlocTransferable implements Transferable {

        DataFlavor[] flavors = new DataFlavor[] { DragAndDropUtil.get().getItemBlocDataFlavor() };
        private ItemBloc itemBloc;

        // ---------------------------------- constructor
        private ItemBlocTransferable(ItemBloc itemBloc) {
            this.itemBloc = itemBloc;
        }

        // --------------------------------- implementing Transferable
        @Override
        public DataFlavor[] getTransferDataFlavors() {
            return flavors;
        }

        @Override
        public boolean isDataFlavorSupported(DataFlavor flavor) {

            return Arrays.asList(flavors).contains(flavor);
        }

        @Override
        public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {

            if (flavor == DragAndDropUtil.get().getItemBlocDataFlavor()) {
                return this.itemBloc;
            } else {
                throw new UnsupportedFlavorException(flavor);
            }
        }

    }

    public static class FileDropper extends DropTargetAdapter {

        private final IMainActionListener actionListener;

        public FileDropper(IMainActionListener actionListener) {
            this.actionListener = actionListener;
        }

        @Override
        public void drop(DropTargetDropEvent dtde) {

            try {
                DropTargetContext context = dtde.getDropTargetContext();
                dtde.acceptDrop(DnDConstants.ACTION_COPY);

                Transferable trans = dtde.getTransferable();
                File file;
                Object obj = trans.getTransferData(DataFlavor.javaFileListFlavor);
                if (obj instanceof List) {
                    List<?> list = (List<?>) obj;
                    for (Object item : list) {
                        if (item instanceof File) {
                            file = (File) item;
                            LOG.config("Drop: " + file.getAbsolutePath());
                            context.dropComplete(true);
                            this.actionListener.actionFileDroppedWithConfirmation(file);
                        }
                    }
                }

            } catch (Exception e) {
                this.actionListener.actionDisplayProcessMessage("ERROR:".concat(e.getMessage()), true);
            }

        }

    }

    public static class ItemBlocTransfertHandler extends TransferHandler {

        /**
         * 
         */
        private static final long serialVersionUID = 1L;

        private final ITreeDndController controller;
        private final IMainActionListener actionListener;

        private ItemBloc itemBlocToDrop;
        private File file;

        public ItemBlocTransfertHandler(IMainActionListener actionListener,
        	 ITreeDndController controller) {
            this.controller = controller;
            this.actionListener = actionListener;
        }
        
        

        @Override
		public int getSourceActions(JComponent c) {
			return TransferHandler.COPY_OR_MOVE;
		}



		@Override
        public boolean importData(TransferSupport support) {
			ItemBloc blocTarget = controller.getTarget(support.getDropLocation().getDropPoint());
            if (itemBlocToDrop != null && blocTarget != null) {
                actionListener.actionItemBlocDroppedWithConfirmation(blocTarget, itemBlocToDrop);
                this.controller.onItemBlocDropEnded();
            }
            else if (file != null) {
                this.actionListener.actionFileDroppedWithConfirmation(file);
                this.controller.onItemBlocDropEnded();
            } 
            else {
                return false;
            }
            return true;
        }

        private void init() {
            this.itemBlocToDrop = null;
            this.file = null;
        }

        private boolean isItemBlocToDrop(TransferSupport support) throws Exception {

        	this.controller.onItemBlocDragStarted();
        	        	
            Transferable trans = support.getTransferable();
            if (trans.isDataFlavorSupported(DragAndDropUtil.get().getItemBlocDataFlavor())) {

                Object obj = trans.getTransferData(DragAndDropUtil.get().getItemBlocDataFlavor());
                if (obj instanceof ItemBloc) {

                    this.itemBlocToDrop = (ItemBloc) obj;

                    if (controller.canPerformAction(itemBlocToDrop, support.getDropLocation().getDropPoint())) {
                       LOG.fine("Drop " + itemBlocToDrop.toString() + " to...");
                        return true;
                    }
                }
            }
            this.controller.onItemBlocDropEnded();
            return false;
        }

//        private boolean isFileToDrop(TransferSupport info) throws Exception {
//
//            Transferable trans = info.getTransferable();
//            if (trans.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
//
//                Object obj = trans.getTransferData(DataFlavor.javaFileListFlavor);
//                if (obj instanceof List) {
//                    List<?> list = (List<?>) obj;
//                    for (Object item : list) {
//                        if (item instanceof File) {
//                            file = (File) item;
//                            LOG.config("Drop: " + file.getAbsolutePath());
//                            return true;
//                        }
//                    }
//                }
//            }
//
//            return false;
//        }

        @Override
        public boolean canImport(TransferSupport info) {

            this.init();
            if (!info.isDrop()) {
                return false;
            }

            try {

                if (this.isItemBlocToDrop(info)) {
                   return true;
                }
                // InvalideDnDOperation !!
//                return  this.dropFile(info);

            } catch (Exception ex) {
            	LOG.severe(ex.toString());
            }
            this.init();
            return false;
        }

    }



}
