package edu.dhbw.andobjviewer.sqllite;

public class Magazine {
	
	private int id;
	private int year;
	private String month;
	
	public Magazine(int id, int year, String month)
	{
		this.id = id;
		this.year = year;
		this.month = month;
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getYear() {
		return year;
	}
	public void setYear(int year) {
		this.year = year;
	}
	public String getMonth() {
		return month;
	}
	public void setMonth(String month) {
		this.month = month;
	}
	
	

}
