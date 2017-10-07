package fr.tsadeo.app.dsntotree.util;

import java.awt.Cursor;
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
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

import javax.swing.TransferHandler;

import fr.tsadeo.app.dsntotree.gui.IMainActionListener;
import fr.tsadeo.app.dsntotree.gui.ItemBlocListener;
import fr.tsadeo.app.dsntotree.gui.MyPanelBloc.PanelChild;
import fr.tsadeo.app.dsntotree.gui.MySimpleTree;
import fr.tsadeo.app.dsntotree.model.ItemBloc;

public class DragAndDropUtil {

    private static final Logger LOG = Logger.getLogger(DragAndDropUtil.class.getName());

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

        public boolean canPerformAction(ItemBloc parentTarget, ItemBloc blocToDrop);

    }

    /**
     * Drag handler for MySimpleTree et MyPanelBloc.PanelChild
     *
     */
    public static class ItemBlocDragHandler {

        public ItemBlocDragHandler(MySimpleTree tree, ItemBlocListener itemBlocListener) {

            DragSource dragSource = DragSource.getDefaultDragSource();
            dragSource.createDefaultDragGestureRecognizer(tree, DnDConstants.ACTION_COPY,
                    new TreeDragSource(tree, itemBlocListener));
        }

        public ItemBlocDragHandler(PanelChild panelChild, ItemBlocListener itemBlocListener) {

            DragSource dragSource = DragSource.getDefaultDragSource();
            dragSource.createDefaultDragGestureRecognizer(panelChild, DnDConstants.ACTION_COPY,
                    new PanelChildDragSource(panelChild, itemBlocListener));

        }
    }

    private static class PanelChildDragSource extends AbstractItemBlocDragSource {

        private PanelChild panelChild;

        private PanelChildDragSource(PanelChild panelChild, ItemBlocListener itemBlocListener) {
            super(itemBlocListener);
            this.panelChild = panelChild;
        }

        @Override
        protected ItemBloc getItemBloc() {
            return panelChild.getItemBloc();
        }
    }

    private static abstract class AbstractItemBlocDragSource extends DragSourceAdapter implements DragGestureListener

    {

        protected Cursor cursor = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR);
        private final ItemBlocListener itemBlocListener;

        protected abstract ItemBloc getItemBloc();

        protected AbstractItemBlocDragSource(ItemBlocListener itemBlocListener) {
            this.itemBlocListener = itemBlocListener;
        }

        // --------------------------- overriding DragSourceAdapter
        @Override
        public void dragEnter(DragSourceDragEvent evt) {
            DragSourceContext ctx = evt.getDragSourceContext();
            ctx.setCursor(cursor);
        }

        @Override
        public void dragExit(DragSourceEvent evt) {
            DragSourceContext ctx = evt.getDragSourceContext();
            ctx.setCursor(DragSource.DefaultCopyNoDrop);
        }

        public void dragDropEnd(DragSourceDropEvent evt) {
            this.itemBlocListener.onItemBlocDropEnded();
            LOG.config("dragDropEnd fired");
        }

        // ------------------------- implementing DragGestureListener
        @Override
        public void dragGestureRecognized(DragGestureEvent evt) {
            ItemBloc itemBloc = this.getItemBloc();
            if (itemBloc != null) {
                LOG.config("startDrag() bloc " + itemBloc.toString());
                this.itemBlocListener.onItemBlocDragStarted();
                evt.startDrag(cursor, null, new Point(0, 0), new ItemBlocTransferable(itemBloc), this);
            }
        }

    }

    private static class TreeDragSource extends AbstractItemBlocDragSource {

        private MySimpleTree tree;

        private TreeDragSource(MySimpleTree tree, ItemBlocListener itemBlocListener) {
            super(itemBlocListener);
            this.tree = tree;
        }

        @Override
        protected ItemBloc getItemBloc() {
            return tree.getItemBlocFromSelection();
        }

    }

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

        private final MySimpleTree tree;
        private final ITreeDndController controller;
        private final IMainActionListener actionListener;

        private ItemBloc itemBlocToDrop;
        private ItemBloc blocTarget;

        public ItemBlocTransfertHandler(MySimpleTree tree, IMainActionListener actionListener,
                ITreeDndController controller) {
            this.tree = tree;
            this.controller = controller;
            this.actionListener = actionListener;
        }

        @Override
        public boolean importData(TransferSupport support) {
            if (itemBlocToDrop != null && blocTarget != null) {
                actionListener.actionItemBlocDroppedWithConfirmation(blocTarget, itemBlocToDrop);
            }
            return true;
        }

        private void init() {
            this.itemBlocToDrop = null;
            this.blocTarget = null;
        }

        @Override
        public boolean canImport(TransferSupport info) {

            this.init();
            if (!info.isDrop()) {
                return false;
            }

            try {
                Transferable trans = info.getTransferable();
                if (trans.isDataFlavorSupported(DragAndDropUtil.get().getItemBlocDataFlavor())) {

                    Object obj = trans.getTransferData(DragAndDropUtil.get().getItemBlocDataFlavor());
                    if (obj instanceof ItemBloc) {

                        this.itemBlocToDrop = (ItemBloc) obj;

                        this.blocTarget = tree.getItemBlocFromLocation(info.getDropLocation().getDropPoint());
                        if (controller.canPerformAction(blocTarget, itemBlocToDrop)) {
                            DragAndDropUtil.LOG.config("Drop " + itemBlocToDrop.toString() + " to...");
                            return true;
                        }
                    }

                }
            } catch (Exception ex) {
            }
            this.init();
            return false;
        }

    }

    // public static class ItemBlocDropper extends DropTargetAdapter {
    //
    // private MySimpleTree tree;
    //
    // public ItemBlocDropper(MySimpleTree tree) {
    // this.tree = tree;
    // }
    //
    // @Override
    // public void dragEnter(DropTargetDragEvent dtde) {
    //
    // DragAndDropUtil.LOG.config("ItemBlocDropper dragEnter");
    //
    // }
    //
    // @Override
    // public void dragExit(DropTargetEvent dte) {
    // DragAndDropUtil.LOG.config("ItemBlocDropper dragExit");
    // }
    //
    // @Override
    // public void drop(DropTargetDropEvent dtde) {
    //
    // try {
    //
    // ItemBloc itemBlocToDrop;
    // Transferable trans = dtde.getTransferable();
    // if
    // (trans.isDataFlavorSupported(DragAndDropUtil.get().getItemBlocDataFlavor()))
    // {
    //
    // Object obj =
    // trans.getTransferData(DragAndDropUtil.get().getItemBlocDataFlavor());
    // if (obj instanceof ItemBloc) {
    //
    // itemBlocToDrop = (ItemBloc) obj;
    // DragAndDropUtil.LOG.config("Drop ItemBloc " + itemBlocToDrop.toString());
    //
    // ItemBloc blocTarget = tree.getItemBlocFromLocation(dtde.getLocation());
    // if (blocTarget != null) {
    // DragAndDropUtil.LOG.config(" to ItemBloc " + blocTarget.toString());
    // // TODO continuer l'implementation
    // dtde.acceptDrop(DnDConstants.ACTION_COPY);
    // dtde.dropComplete(true);
    // } else {
    // DragAndDropUtil.LOG.config(" pas de destination!!");
    // dtde.dropComplete(false);
    // }
    // } else {
    // dtde.dropComplete(false);
    // }
    //
    // }
    //
    // } catch (Exception ex) {
    // DragAndDropUtil.LOG.severe("Error in ItemBlocDropper.drop(): " +
    // ex.toString());
    // dtde.rejectDrop();
    // dtde.dropComplete(false);
    // }
    //
    // }
    //
    // }

}
