package com.useful.ucars;

public class Colors {
	private String success = "";
	private String error = "";
	private String info = "";
	private String title = "";
	private String tp = "";

	public Colors(String success, String error, String info, String title,
			String tp) {
		this.success = UCars.colorise(success);
		this.error = UCars.colorise(error);
		this.info = UCars.colorise(info);
		this.title = UCars.colorise(title);
		this.tp = UCars.colorise(tp);
	}

	public String getSuccess() {
		return this.success;
	}

	public String getError() {
		return this.error;
	}

	public String getInfo() {
		return this.info;
	}

	public String getTitle() {
		return this.title;
	}

	public String getTp() {
		return this.tp;
	}
}
