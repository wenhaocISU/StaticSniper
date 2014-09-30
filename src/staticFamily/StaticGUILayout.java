package staticFamily;

import java.util.ArrayList;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class StaticGUILayout {

	private Node Node;
	private String Name;
	private String Type;
	private ArrayList<StaticGUINode> viewNodes;
	private boolean isCustomLayout;
	private boolean hasInclude;

	public StaticGUILayout(String name, Node layoutNode, String type,
			boolean isCustom) {
		Name = name;
		viewNodes = new ArrayList<StaticGUINode>();
		Node = layoutNode;
		isCustomLayout = isCustom;
		hasInclude = false;
		Type = type;
		checkInclude();
	}

	private void checkInclude() {
		Element e = (Element) Node;
		NodeList nl = e.getElementsByTagName("*");
		for (int i = 0, len = nl.getLength(); i < len; i++)
			if (nl.item(i).getNodeName().equals("include")) {
				hasInclude = true;
				return;
			}
	}

	public String getType() {
		return Type;
	}

	public void addNode(StaticGUINode node) {
		viewNodes.add(node);
	}

	public boolean isCustomLayout() {
		return isCustomLayout;
	}

	public boolean hasInclude() {
		return hasInclude;
	}

	public ArrayList<StaticGUINode> getAllViewNodes() {
		return viewNodes;
	}

	public Node getNode() {
		return Node;
	}

	public String getName() {
		return Name;
	}

	public StaticGUINode getViewNodeById(String id) {
		for (StaticGUINode vN : viewNodes)
			if (vN.getID().equals(id))
				return vN;
		return null;
	}

	public ArrayList<StaticGUINode> getLeavingViewNodes() {
		ArrayList<StaticGUINode> result = new ArrayList<StaticGUINode>();
		for (StaticGUINode vN : viewNodes)
			if (vN.hasLeavingEventHandlers())
				result.add(vN);
		return result;
	}

	public ArrayList<StaticGUINode> getStayingViewNodes() {
		ArrayList<StaticGUINode> result = new ArrayList<StaticGUINode>();
		for (StaticGUINode vN : viewNodes)
			if (!vN.hasLeavingEventHandlers())
				result.add(vN);
		return result;
	}

}
