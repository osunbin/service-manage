package com.bin.webmonitor.model;

import com.bin.webmonitor.component.StrJson;

import java.util.ArrayList;
import java.util.List;

public class JsTreeNode extends StrJson {

	public static JsTreeNode build() {
		return new JsTreeNode();
	}

	private Long id = 0L;
	private String text = "";
	private String icon = "";
	private State state = new State();
	private List<JsTreeNode> children = new ArrayList<>();

	public Long getId() {
		return id;
	}

	public JsTreeNode setId(Long id) {
		this.id = id;
		return this;
	}

	public String getText() {
		return text;
	}

	public JsTreeNode setText(String text) {
		this.text = text;
		return this;
	}

	public String getIcon() {
		return icon;
	}

	public JsTreeNode setIcon(String icon) {
		this.icon = icon;
		return this;
	}

	public State getState() {
		return state;
	}

	public JsTreeNode setState(State state) {
		this.state = state;
		return this;
	}

	public List<JsTreeNode> getChildren() {
		return children;
	}

	public JsTreeNode setChildren(List<JsTreeNode> children) {
		this.children = children;
		return this;
	}
	
	public JsTreeNode addChild(JsTreeNode jsTreeNode) {
		this.children.add(jsTreeNode);
		return this;
	}




	public static class State {

		private boolean opened = false;
		private boolean disabled = false;
		private boolean selected = false;

		public boolean getOpened() {
			return opened;
		}
		public void setOpened(boolean opened) {
			this.opened = opened;
		}
		public boolean getDisabled() {
			return disabled;
		}
		public void setDisabled(boolean disabled) {
			this.disabled = disabled;
		}
		public boolean getSelected() {
			return selected;
		}
		public void setSelected(boolean selected) {
			this.selected = selected;
		}

	}
}
