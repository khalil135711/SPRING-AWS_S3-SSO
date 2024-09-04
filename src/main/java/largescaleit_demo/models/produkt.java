package largescaleit_demo.models;

import java.util.UUID;


public class produkt {

	public UUID idD;
	public String nameD;
	public String typeD;
	public String imageUrl;
	public double price;
	
	public UUID getIdD() {
		return idD;
	}
	public void setIdD(UUID idD) {
		this.idD = idD;
	}
	public String getNameD() {
		return nameD;
	}
	public void setNameD(String nameD) {
		this.nameD = nameD;
	}
	public String getTypeD() {
		return typeD;
	}
	public void setTypeD(String typeD) {
		this.typeD = typeD;
	}
	public String getImageUrl() {
		return imageUrl;
	}
	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}
	public double getPrice() {
		return price;
	}
	public void setPrice(double price) {
		this.price = price;
	}

	
	
	
}
