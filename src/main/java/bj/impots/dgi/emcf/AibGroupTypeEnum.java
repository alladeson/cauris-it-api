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
import com.google.gson.annotations.SerializedName;
import java.io.IOException;
import com.google.gson.TypeAdapter;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

/**
 * Gets or Sets AibGroupTypeEnum
 */
@JsonAdapter(AibGroupTypeEnum.Adapter.class)
public enum AibGroupTypeEnum {
  A("A"),
  B("B");

  private String value;

  AibGroupTypeEnum(String value) {
    this.value = value;
  }

  public String getValue() {
    return value;
  }

  @Override
  public String toString() {
    return String.valueOf(value);
  }

  public static AibGroupTypeEnum fromValue(String text) {
    for (AibGroupTypeEnum b : AibGroupTypeEnum.values()) {
      if (String.valueOf(b.value).equals(text)) {
        return b;
      }
    }
    return null;
  }

  public static class Adapter extends TypeAdapter<AibGroupTypeEnum> {
    @Override
    public void write(final JsonWriter jsonWriter, final AibGroupTypeEnum enumeration) throws IOException {
      jsonWriter.value(enumeration.getValue());
    }

    @Override
    public AibGroupTypeEnum read(final JsonReader jsonReader) throws IOException {
      String value = jsonReader.nextString();
      return AibGroupTypeEnum.fromValue(String.valueOf(value));
    }
  }
}
