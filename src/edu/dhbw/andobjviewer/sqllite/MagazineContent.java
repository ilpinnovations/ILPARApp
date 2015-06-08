package edu.dhbw.andobjviewer.sqllite;

public class MagazineContent {
	
	private int contentId;
	private String headLine;
	private String image;
	private String content;
	private int magazineId;
	
	public MagazineContent(int contentId, String headLine, String image, String content, int magazineId) {
		
		this.contentId = contentId;
		this.headLine = headLine;
		this.image = image;
		this.content = content;
		this.magazineId = magazineId;
	}
	
	public MagazineContent(int contentId, String headLine, String content, int magazineId) {
		
		this.contentId = contentId;
		this.headLine = headLine;
		this.content = content;
		this.magazineId = magazineId;
	}
	
	public int getContentId() {
		return contentId;
	}
	public void setContentId(int contentId) {
		this.contentId = contentId;
	}
	public String getHeadLine() {
		return headLine;
	}
	public void setHeadLine(String headLine) {
		this.headLine = headLine;
	}
	public String getImage() {
		return image;
	}
	public void setImage(String image) {
		this.image = image;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public int getMagazineId() {
		return magazineId;
	}
	public void setMagazineId(int magazineId) {
		this.magazineId = magazineId;
	}
	
}
