/*
 * e-MCF
 * DGI Bénin - Tous droits réservés
 *
 * OpenAPI spec version: 1.0
 * 
 *
 * NOTE: This class is auto generated by the swagger code generator program.
 * https://github.com/swagger-api/swagger-codegen.git
 * Do not edit the class manually.
 */

package bj.impots.dgi.emcf;

import java.util.Objects;
import java.util.Arrays;
import com.google.gson.TypeAdapter;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.IOException;
/**
 * EmcfInfoDto
 */

@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.JavaClientCodegen", date = "2021-01-06T15:15:09.028+01:00[Europe/Prague]")
public class EmcfInfoDto {
  @SerializedName("nim")
  private String nim = null;

  @SerializedName("status")
  private String status = null;

  @SerializedName("shopName")
  private String shopName = null;

  @SerializedName("address1")
  private String address1 = null;

  @SerializedName("address2")
  private String address2 = null;

  @SerializedName("address3")
  private String address3 = null;

  @SerializedName("contact1")
  private String contact1 = null;

  @SerializedName("contact2")
  private String contact2 = null;

  @SerializedName("contact3")
  private String contact3 = null;

  public EmcfInfoDto nim(String nim) {
    this.nim = nim;
    return this;
  }

   /**
   * Get nim
   * @return nim
  **/
  @Schema(description = "")
  public String getNim() {
    return nim;
  }

  public void setNim(String nim) {
    this.nim = nim;
  }

  public EmcfInfoDto status(String status) {
    this.status = status;
    return this;
  }

   /**
   * Get status
   * @return status
  **/
  @Schema(description = "")
  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public EmcfInfoDto shopName(String shopName) {
    this.shopName = shopName;
    return this;
  }

   /**
   * Get shopName
   * @return shopName
  **/
  @Schema(description = "")
  public String getShopName() {
    return shopName;
  }

  public void setShopName(String shopName) {
    this.shopName = shopName;
  }

  public EmcfInfoDto address1(String address1) {
    this.address1 = address1;
    return this;
  }

   /**
   * Get address1
   * @return address1
  **/
  @Schema(description = "")
  public String getAddress1() {
    return address1;
  }

  public void setAddress1(String address1) {
    this.address1 = address1;
  }

  public EmcfInfoDto address2(String address2) {
    this.address2 = address2;
    return this;
  }

   /**
   * Get address2
   * @return address2
  **/
  @Schema(description = "")
  public String getAddress2() {
    return address2;
  }

  public void setAddress2(String address2) {
    this.address2 = address2;
  }

  public EmcfInfoDto address3(String address3) {
    this.address3 = address3;
    return this;
  }

   /**
   * Get address3
   * @return address3
  **/
  @Schema(description = "")
  public String getAddress3() {
    return address3;
  }

  public void setAddress3(String address3) {
    this.address3 = address3;
  }

  public EmcfInfoDto contact1(String contact1) {
    this.contact1 = contact1;
    return this;
  }

   /**
   * Get contact1
   * @return contact1
  **/
  @Schema(description = "")
  public String getContact1() {
    return contact1;
  }

  public void setContact1(String contact1) {
    this.contact1 = contact1;
  }

  public EmcfInfoDto contact2(String contact2) {
    this.contact2 = contact2;
    return this;
  }

   /**
   * Get contact2
   * @return contact2
  **/
  @Schema(description = "")
  public String getContact2() {
    return contact2;
  }

  public void setContact2(String contact2) {
    this.contact2 = contact2;
  }

  public EmcfInfoDto contact3(String contact3) {
    this.contact3 = contact3;
    return this;
  }

   /**
   * Get contact3
   * @return contact3
  **/
  @Schema(description = "")
  public String getContact3() {
    return contact3;
  }

  public void setContact3(String contact3) {
    this.contact3 = contact3;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    EmcfInfoDto emcfInfoDto = (EmcfInfoDto) o;
    return Objects.equals(this.nim, emcfInfoDto.nim) &&
        Objects.equals(this.status, emcfInfoDto.status) &&
        Objects.equals(this.shopName, emcfInfoDto.shopName) &&
        Objects.equals(this.address1, emcfInfoDto.address1) &&
        Objects.equals(this.address2, emcfInfoDto.address2) &&
        Objects.equals(this.address3, emcfInfoDto.address3) &&
        Objects.equals(this.contact1, emcfInfoDto.contact1) &&
        Objects.equals(this.contact2, emcfInfoDto.contact2) &&
        Objects.equals(this.contact3, emcfInfoDto.contact3);
  }

  @Override
  public int hashCode() {
    return Objects.hash(nim, status, shopName, address1, address2, address3, contact1, contact2, contact3);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class EmcfInfoDto {\n");
    
    sb.append("    nim: ").append(toIndentedString(nim)).append("\n");
    sb.append("    status: ").append(toIndentedString(status)).append("\n");
    sb.append("    shopName: ").append(toIndentedString(shopName)).append("\n");
    sb.append("    address1: ").append(toIndentedString(address1)).append("\n");
    sb.append("    address2: ").append(toIndentedString(address2)).append("\n");
    sb.append("    address3: ").append(toIndentedString(address3)).append("\n");
    sb.append("    contact1: ").append(toIndentedString(contact1)).append("\n");
    sb.append("    contact2: ").append(toIndentedString(contact2)).append("\n");
    sb.append("    contact3: ").append(toIndentedString(contact3)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(java.lang.Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }

}
