package ecofarm.bean;

import java.util.ArrayList;

import ecofarm.entity.Province;
import ecofarm.entity.District;
import ecofarm.entity.Ward;

public class AddressDatasBean {
	public class ProvinceBean {
		private int id;
		private String name;
		private ArrayList<DistrictBean> districts;

		public ProvinceBean() {
		}

		public ProvinceBean(int id, String name) {
			this.id = id;
			this.name = name;
			this.districts = new ArrayList<>();
		}

		public int getId() {
			return id;
		}

		public void setId(int id) {
			this.id = id;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public ArrayList<DistrictBean> getDistricts() {
			return districts;
		}

		public void setDistricts(ArrayList<DistrictBean> districts) {
			this.districts = districts;
		}

		// constructor, getters and setters
	}

	public class DistrictBean {
		private int id;
		private String name;
		private ProvinceBean province;
		private ArrayList<WardBean> wards;

		public DistrictBean() {
		}

		public DistrictBean(int id, String name, ProvinceBean province) {
			this.id = id;
			this.name = name;
			this.province = province;
			this.wards = new ArrayList<>();
		}

		public int getId() {
			return id;
		}

		public void setId(int id) {
			this.id = id;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public ProvinceBean getProvince() {
			return province;
		}

		public void setProvince(ProvinceBean province) {
			this.province = province;
		}

		public ArrayList<WardBean> getWards() {
			return wards;
		}

		public void setWards(ArrayList<WardBean> wards) {
			this.wards = wards;
		}

		public int getProvinceId() {
			return province.getId();
		}
		// constructor, getters and setters
	}

	public class WardBean {
		private int id;
		private String name;
		private DistrictBean district;

		public WardBean() {
		}

		public WardBean(int id, String name, DistrictBean district) {
			this.id = id;
			this.name = name;
			this.district = district;
		}

		public int getId() {
			return id;
		}

		public void setId(int id) {
			this.id = id;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public DistrictBean getDistrict() {
			return district;
		}

		public void setDistrict(DistrictBean district) {
			this.district = district;
		}

		public int getDistrictId() {
			return district.getId();
		}
		// constructor, getters and setters
	}

	public AddressBean ConvertToDataAddressBean(ArrayList<Province> provinceList) {
		AddressBean datasAddress = new AddressBean();
		for (Province province : provinceList) {
			ProvinceBean provinceBean = new ProvinceBean(province.getProvinceId(), province.getName());
			datasAddress.getProvinceBean().add(provinceBean);
			for (District district : province.getDistricts()) {
				DistrictBean districtBean = new DistrictBean(district.getDistrictId(), district.getName(),
						provinceBean);
				datasAddress.getDistrictBean().add(districtBean);
				for (Ward ward : district.getWards()) {
					WardBean wardBean = new WardBean(ward.getWardId(), ward.getName(), districtBean);
					datasAddress.getWardBean().add(wardBean);
				}
			}

		}
		return datasAddress;
	}

}
