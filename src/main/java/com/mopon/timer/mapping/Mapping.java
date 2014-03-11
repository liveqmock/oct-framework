package com.mopon.timer.mapping;

/**
 * quartz trigger bean ID 与 名称映射关系实体
 * @author liuguomin
 *
 */
public class Mapping {

	/**
	 * quartz_config.xml中 mapping的ID
	 */
	private String id;
	/**
	 * quartz_config.xml中 mapping的name
	 */
	private String name;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	
}
