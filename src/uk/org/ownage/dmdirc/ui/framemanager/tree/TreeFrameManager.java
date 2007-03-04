/*
 * Copyright (c) 2006-2007 Chris Smith, Shane Mc Cormack, Gregory Holmes
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package uk.org.ownage.dmdirc.ui.framemanager.tree;

import java.awt.FlowLayout;
import java.util.Hashtable;
import javax.swing.JComponent;
import javax.swing.JTree;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.event.TreeWillExpandListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.ExpandVetoException;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import uk.org.ownage.dmdirc.Channel;
import uk.org.ownage.dmdirc.FrameContainer;
import uk.org.ownage.dmdirc.Query;
import uk.org.ownage.dmdirc.Raw;
import uk.org.ownage.dmdirc.Server;
import uk.org.ownage.dmdirc.logger.ErrorLevel;
import uk.org.ownage.dmdirc.logger.Logger;
import uk.org.ownage.dmdirc.ui.framemanager.FrameManager;

/**
 * Manages open windows in the application in a tree style view
 */
public class TreeFrameManager implements FrameManager, TreeModelListener,
        TreeSelectionListener, TreeExpansionListener, TreeWillExpandListener {
    
    /**
     * display tree
     */
    private JTree tree;
    
    /**
     * root node
     */
    private DefaultMutableTreeNode root;
    
    /**
     * node renderer
     */
    private TreeViewTreeCellRenderer renderer;
    
    /**
     * data model
     */
    private DefaultTreeModel model;
    
    /**
     * node storage, used for adding and deleting nodes correctly
     */
    private Hashtable<FrameContainer, DefaultMutableTreeNode> nodes;
    
    /**
     * creates a new instance of the TreeFrameManager
     */
    public TreeFrameManager() {
        nodes = new Hashtable<FrameContainer, DefaultMutableTreeNode>();
        root = new DefaultMutableTreeNode("DMDirc");
        model = new DefaultTreeModel(root);
        tree = new JTree(model);
        tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        tree.addTreeSelectionListener(this);
        renderer = new TreeViewTreeCellRenderer();
        tree.setCellRenderer(renderer);
        tree.setRootVisible(false);
    }
    
    /**
     * adds a server to the tree
     * @param server associated server
     */
    public void addServer(Server server) {
        DefaultMutableTreeNode node = new DefaultMutableTreeNode();
        nodes.put(server, node);
        node.setUserObject(server);
        model.insertNodeInto(node, (MutableTreeNode)root, root.getChildCount());
        tree.scrollPathToVisible(new TreePath(node.getPath()));
    }
    
    /**
     * removes a server from the tree
     * @param server associated server
     */
    public void delServer(Server server) {
        model.removeNodeFromParent(nodes.get(server));
    }
    
    /**
     * adds a channel to the tree
     * @param server associated server
     * @param channel associated framecontainer
     */
    public void addChannel(Server server, Channel channel) {
        DefaultMutableTreeNode node = new DefaultMutableTreeNode();
        nodes.put(channel, node);
        node.setUserObject(channel);
        model.insertNodeInto(node, (MutableTreeNode)nodes.get(server), nodes.get(server).getChildCount());
        tree.scrollPathToVisible(new TreePath(node.getPath()));
    }
    
    /**
     * deletes a channel from the tree
     * @param server associated server
     * @param channel associated framecontainer
     */
    public void delChannel(Server server, Channel channel) {
        model.removeNodeFromParent(nodes.get(channel));
    }
    
    /**
     * adds a query to the tree
     * @param server associated server
     * @param query associated framecontainer
     */
    public void addQuery(Server server, Query query) {
        DefaultMutableTreeNode node = new DefaultMutableTreeNode();
        nodes.put(query, node);
        node.setUserObject(query);
        model.insertNodeInto(node, (MutableTreeNode)nodes.get(server), nodes.get(server).getChildCount());
        tree.scrollPathToVisible(new TreePath(node.getPath()));
    }
    
    /**
     * deletes a query from the tree
     * @param server associated server
     * @param query associated framecontainer
     */
    public void delQuery(Server server, Query query) {
        model.removeNodeFromParent(nodes.get(query));
    }
    
    /**
     * adds a raw to the tree
     * @param server associated server
     * @param raw associated framecontainer
     */
    public void addRaw(Server server, Raw raw) {
        DefaultMutableTreeNode node = new DefaultMutableTreeNode();
        nodes.put(raw, node);
        node.setUserObject(raw);
        model.insertNodeInto(node, (MutableTreeNode)nodes.get(server), nodes.get(server).getChildCount());
        tree.scrollPathToVisible(new TreePath(node.getPath()));
    }
    
    /**
     * deletes a raw from the tree
     * @param server associated server
     * @param raw associated framecontainer
     */
    public void delRaw(Server server, Raw raw) {
        model.removeNodeFromParent(nodes.get(raw));
    }
    
    /**
     * valled whenever the value of the selection changes
     * @param e selection event
     */
    public void valueChanged(TreeSelectionEvent e) {
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
        
        if (node == null) return;
        
        Object nodeInfo = node.getUserObject();
        if (nodeInfo instanceof FrameContainer) {
            ((FrameContainer)nodeInfo).activateFrame();
        } else {
            Logger.error(ErrorLevel.WARNING, "Unknown node type.");
        }
    }
    
    /**
     * Called after the tree has been expanded
     * @param event expansion event
     */
    public void treeExpanded(TreeExpansionEvent event) {
    }
    
    /**
     * Called after the tree has been collapsed
     * @param event expansion event
     */
    public void treeCollapsed(TreeExpansionEvent event) {
    }
    
    /**
     * Called when the tree is about to expand
     * @param event expansion event
     * @throws javax.swing.tree.ExpandVetoException thrown to prevent node expanding
     */
    public void treeWillExpand(TreeExpansionEvent event) throws ExpandVetoException {
    }
    
    /**
     * Called when the tree is about to collapse
     * @param event expansion event
     * @throws javax.swing.tree.ExpandVetoException throw to prevent node collapsing
     */
    public void treeWillCollapse(TreeExpansionEvent event) throws ExpandVetoException {
    }
    
    /**
     * Sets the parent component in the main UI
     * @param parent parent component
     */
    public void setParent(JComponent parent) {
        parent.setLayout(new FlowLayout());
        parent.add(tree);
        tree.setVisible(true);
        parent.setVisible(true);
    }
    
    /**
     * called after a node, or set of nodes, changes
     * @param e change event
     */
    public void treeNodesChanged(TreeModelEvent e) {
    }
    
    /**
     * called after a node has been inserted into the tree
     * @param e change event
     */
    public void treeNodesInserted(TreeModelEvent e) {
    }
    
    /**
     * Called when a node is removed from the tree
     * @param e change event
     */
    public void treeNodesRemoved(TreeModelEvent e) {
    }
    
    /**
     * Called when a tree changes structure
     * @param e change event
     */
    public void treeStructureChanged(TreeModelEvent e) {
    }
}
