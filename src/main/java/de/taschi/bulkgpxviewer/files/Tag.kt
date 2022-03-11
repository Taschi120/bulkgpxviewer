package de.taschi.bulkgpxviewer.files;

import lombok.Data;

@Data
public class Tag {
	private String name;
	private boolean userDefined;
	
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
}
