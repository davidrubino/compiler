package trad;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import org.antlr.v4.runtime.tree.Tree;
import org.antlr.v4.runtime.tree.gui.TreeViewer;

public class MyTreeViewer extends TreeViewer {
	private static final long serialVersionUID = 1L;

	private ArrayList<Tree> nodes;
	private JTree jTree;
	private Map<Tree, TreeNodeWrapper> map = new HashMap<Tree, TreeNodeWrapper>();

	// Fonction récursive
	private void walk(Tree tree) {
		nodes.add(tree);

		for (int i = 0; i < tree.getChildCount(); i++)
			walk(tree.getChild(i));
	}

	private Tree getSelectedNodes(MouseEvent e) {
		for (Tree tree : nodes) {
			Rectangle2D bounds = getBoundsOfNode(tree);

			Rectangle2D newBounds = new Rectangle2D.Double(
					bounds.getX() * getScale(),
					bounds.getY() * getScale(),
					bounds.getWidth() * getScale(),
					bounds.getHeight() * getScale());

			if (newBounds.contains(e.getPoint())) {
				return tree;
			}
		}
		return null;
	}

	@Override
	public void setTree(Tree tree) {
		nodes = new ArrayList<Tree>();

		if (tree != null)
			walk(tree);

		super.setTree(tree);
	}

	public MyTreeViewer(List<String> ruleNames, Tree tree) {
        super(ruleNames, tree);

		addMouseMotionListener(new MouseMotionAdapter() {
			public void mouseMoved(MouseEvent e) {
				addHighlightedNodes(Arrays.<Tree>asList());
				repaint();

				Tree selectedNode = getSelectedNodes(e);
				if (selectedNode != null) {
					addHighlightedNodes(Arrays.asList(selectedNode));
					repaint();
				}
			}	
		});

		addMouseListener(new MouseAdapter() {		
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON1) {

					Tree selectedNode  = getSelectedNodes(e);

					if (selectedNode != null) {
						setTree(selectedNode);
						jTree.getSelectionModel().setSelectionPath(new TreePath(map.get(selectedNode).getPath()));
						repaint();
					}
				}
			}
		}); 

		showInDialog();
	}

	protected JDialog showInDialog() {
		final JDialog dialog = new JDialog();
		dialog.setTitle("Parse Tree Inspector");

		// Make new content panes
		final Container mainPane = new JPanel(new BorderLayout(5,5));
		final Container contentPane = new JPanel(new BorderLayout(0,0));
		contentPane.setBackground(Color.white);

		// Wrap viewer in scroll pane
		JScrollPane scrollPane = new JScrollPane(this);
		// Make the scrollpane (containing the viewer) the center component
		contentPane.add(scrollPane, BorderLayout.CENTER);

		JPanel wrapper = new JPanel(new FlowLayout());

		// Add button to bottom
		JPanel bottomPanel = new JPanel(new BorderLayout(0,0));
		contentPane.add(bottomPanel, BorderLayout.SOUTH);

		JButton ok = new JButton("OK");
		ok.addActionListener(
				new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						dialog.setVisible(false);
						dialog.dispose();
					}
				}
				);
		wrapper.add(ok);
		
		bottomPanel.add(wrapper, BorderLayout.SOUTH);

		// Add scale slider
		int sliderValue = (int) ((this.getScale()-1.0) * 1000);
		final JSlider scaleSlider = new JSlider(JSlider.HORIZONTAL,
				-999,1000,sliderValue);
		scaleSlider.addChangeListener(
				new ChangeListener() {
					@Override
					public void stateChanged(ChangeEvent e) {
						int v = scaleSlider.getValue();
						MyTreeViewer.this.setScale(v / 1000.0 + 1.0);
					}
				}
				);
		bottomPanel.add(scaleSlider, BorderLayout.CENTER);

		// Add a JTree representing the parser tree of the input.
		JPanel treePanel = new JPanel(new BorderLayout(5, 5));

		Tree parseTreeRoot = this.getTree().getRoot();
		TreeNodeWrapper nodeRoot = new TreeNodeWrapper(parseTreeRoot, this);
		
		fillTree(nodeRoot, parseTreeRoot);
		
		this.jTree = new JTree(nodeRoot);
		this.jTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);

		this.jTree.addTreeSelectionListener(new TreeSelectionListener() {
			@Override
			public void valueChanged(TreeSelectionEvent e) {

				JTree selectedTree = (JTree) e.getSource();
				TreePath path = selectedTree.getSelectionPath();
				TreeNodeWrapper treeNode = (TreeNodeWrapper) path.getLastPathComponent();

				// Set the clicked AST.
				MyTreeViewer.this.setTree((Tree) treeNode.getUserObject());
			}
		});

		treePanel.add(new JScrollPane(this.jTree));

		// Create the pane for both the JTree and the AST
		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
				treePanel, contentPane);

		mainPane.add(splitPane, BorderLayout.CENTER);

		dialog.setContentPane(mainPane);

		// make viz
		dialog.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		dialog.setPreferredSize(new Dimension(600, 500));
		dialog.pack();

		// After pack(): set the divider at 1/3 of the frame.
		splitPane.setDividerLocation(0.33);

		dialog.setLocationRelativeTo(null);
		dialog.setVisible(true);
		return dialog;
	}
	
	private void fillTree(TreeNodeWrapper node, Tree tree) {

		if (tree == null) {
			return;
		}

		for (int i = 0; i < tree.getChildCount(); i++) {
			Tree childTree = tree.getChild(i);
			TreeNodeWrapper childNode = new TreeNodeWrapper(childTree, this);
			map.put(childTree, childNode);
			node.add(childNode);

			fillTree(childNode, childTree);
		}
	}
	
	private class TreeNodeWrapper extends DefaultMutableTreeNode {
        private static final long serialVersionUID = 1L;

        final MyTreeViewer viewer;

		TreeNodeWrapper(Tree tree, MyTreeViewer viewer) {
			super(tree);
			this.viewer = viewer;
		}

		@Override
		public String toString() {
			return viewer.getText((Tree) this.getUserObject());
		}
	}
}
