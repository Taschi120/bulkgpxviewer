package de.taschi.bulkgpxviewer.files;

public class Tag {
	private String name;
	private boolean userDefined;

	public Tag() {
	}

	@Override
	public int hashCode() {
		return name.hashCode();
	}
	
	@Override
	public boolean equals(Object o) {
		if(o != null && o.getClass() == Tag.class) {
			var t = (Tag) o;
			return this.getName().equals(t.getName());
		} else {
			return false;
		}
	}

	public String getName() {
		return this.name;
	}

	public boolean isUserDefined() {
		return this.userDefined;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setUserDefined(boolean userDefined) {
		this.userDefined = userDefined;
	}

	public String toString() {
		return "Tag(name=" + this.getName() + ", userDefined=" + this.isUserDefined() + ")";
	}
}
