package ru.will0376.cases.pojo;

public class Case {
	private String name;
	private int available;
	private String texture;

	public Case(String name, int available, String texture) {
		this.name = name;
		this.available = available;
		this.texture = texture;
	}

	public String getName() {
		return this.name;
	}

	public int getAvailable() {
		return this.available;
	}

	public String getTexture() {
		return this.texture;
	}
}
