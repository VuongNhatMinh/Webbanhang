package ecofarm.entity;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
//@Table(name = "Address", schema = "dbo", catalog = "DB_Webns")
@Table(name = "Address", schema = "dbo", catalog = "DB_Webnongsan")
public class Address {

	@Id
	@GeneratedValue
	@Column(name = "AddressID", unique = true, nullable = false)
	private int addressId;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "WardID", nullable = false)
	private Ward ward;

	@Column(name = "Name")
	private String name;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "AccountID", nullable = false)
	private Account account;

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "defaultAddress")
	private Set<Account> accounts = new HashSet<>(0);

	public Address() {

	}

	public Address(Ward ward, Account account) {
		this.ward = ward;
		this.account = account;

	}
	public Address(Ward ward, Account account,String name) {
		this.ward = ward;
		this.account = account;
		this.name = name;

	}

	public Address(int addressId, Ward ward, String name) {
		this.addressId = addressId;
		this.ward = ward;
		this.name = name;
	}

	public int getAddressId() {
		return this.addressId;
	}

	public void setAddressId(int addressId) {
		this.addressId = addressId;
	}

	public Ward getWard() {
		return this.ward;
	}

	public Set<Account> getAccounts() {
		return accounts;
	}

	public void setAccounts(Set<Account> accounts) {
		this.accounts = accounts;
	}

	public void setWard(Ward ward) {
		this.ward = ward;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Account getAccount() {
		return account;
	}

	public void setAccount(Account account) {
		this.account = account;
	}

	public String getFullAddress() {
		return getName() + ", " + getWard().getName() + ", " + getWard().getDistrict().getName() + ", "
				+ getWard().getDistrict().getProvince().getName();
	}

}
