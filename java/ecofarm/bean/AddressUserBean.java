package ecofarm.bean;

import javax.validation.constraints.NotNull;

public class AddressUserBean {
	@NotNull
	private int wardId;
	private int districtId;
	private int provinceId;
	private String addressLine;

	public int getWardId() {
		return wardId;
	}

	public AddressUserBean() {
		wardId = -1;
		districtId = -1;
		provinceId = -1;
		addressLine = "";

	}

	public void setWardId(int wardId) {
		this.wardId = wardId;
	}

	public AddressUserBean(int wardId, int districtId, int provinceId, String addressLine) {
		this.wardId = wardId;
		this.districtId = districtId;
		this.provinceId = provinceId;
		this.addressLine = addressLine;
	}

	public int getDistrictId() {
		return districtId;
	}

	public void setDistrictId(int districtId) {
		this.districtId = districtId;
	}

	public int getProvinceId() {
		return provinceId;
	}

	public void setProvinceId(int provinceId) {
		this.provinceId = provinceId;
	}

	public String getAddressLine() {
		return addressLine;
	}

	public void setAddressLine(String addressLine) {
		this.addressLine = addressLine;
	}
}