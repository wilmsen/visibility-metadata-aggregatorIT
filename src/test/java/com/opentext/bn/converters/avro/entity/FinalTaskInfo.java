/**
 * Autogenerated by Avro
 *
 * DO NOT EDIT DIRECTLY
 */
package com.opentext.bn.converters.avro.entity;

import org.apache.avro.specific.SpecificData;
import org.apache.avro.message.BinaryMessageEncoder;
import org.apache.avro.message.BinaryMessageDecoder;
import org.apache.avro.message.SchemaStore;

@SuppressWarnings("all")
@org.apache.avro.specific.AvroGenerated
public class FinalTaskInfo extends org.apache.avro.specific.SpecificRecordBase implements org.apache.avro.specific.SpecificRecord {
  private static final long serialVersionUID = -9109024999431800587L;
  public static final org.apache.avro.Schema SCHEMA$ = new org.apache.avro.Schema.Parser().parse("{\"type\":\"record\",\"name\":\"FinalTaskInfo\",\"namespace\":\"com.opentext.bn.converters.avro.entity\",\"fields\":[{\"name\":\"transactionStatus\",\"type\":{\"type\":\"enum\",\"name\":\"TransactionStatus\",\"symbols\":[\"PROCESSING\",\"DELIVERY_IN_PROGRESS\",\"DELIVERY_DISABLED\",\"READY_FOR_PICKUP\",\"BATCHED\",\"DELIVERED\",\"ON_HOLD\",\"DUPLICATED\",\"RECEIVER_ERROR\",\"DELIVERY_ERROR\",\"VALIDATION_ERROR\",\"FAILED\"]}}]}");
  public static org.apache.avro.Schema getClassSchema() { return SCHEMA$; }

  private static SpecificData MODEL$ = new SpecificData();

  private static final BinaryMessageEncoder<FinalTaskInfo> ENCODER =
      new BinaryMessageEncoder<FinalTaskInfo>(MODEL$, SCHEMA$);

  private static final BinaryMessageDecoder<FinalTaskInfo> DECODER =
      new BinaryMessageDecoder<FinalTaskInfo>(MODEL$, SCHEMA$);

  /**
   * Return the BinaryMessageDecoder instance used by this class.
   */
  public static BinaryMessageDecoder<FinalTaskInfo> getDecoder() {
    return DECODER;
  }

  /**
   * Create a new BinaryMessageDecoder instance for this class that uses the specified {@link SchemaStore}.
   * @param resolver a {@link SchemaStore} used to find schemas by fingerprint
   */
  public static BinaryMessageDecoder<FinalTaskInfo> createDecoder(SchemaStore resolver) {
    return new BinaryMessageDecoder<FinalTaskInfo>(MODEL$, SCHEMA$, resolver);
  }

  /** Serializes this FinalTaskInfo to a ByteBuffer. */
  public java.nio.ByteBuffer toByteBuffer() throws java.io.IOException {
    return ENCODER.encode(this);
  }

  /** Deserializes a FinalTaskInfo from a ByteBuffer. */
  public static FinalTaskInfo fromByteBuffer(
      java.nio.ByteBuffer b) throws java.io.IOException {
    return DECODER.decode(b);
  }

   private com.opentext.bn.converters.avro.entity.TransactionStatus transactionStatus;

  /**
   * Default constructor.  Note that this does not initialize fields
   * to their default values from the schema.  If that is desired then
   * one should use <code>newBuilder()</code>.
   */
  public FinalTaskInfo() {}

  /**
   * All-args constructor.
   * @param transactionStatus The new value for transactionStatus
   */
  public FinalTaskInfo(com.opentext.bn.converters.avro.entity.TransactionStatus transactionStatus) {
    this.transactionStatus = transactionStatus;
  }

  public org.apache.avro.Schema getSchema() { return SCHEMA$; }
  // Used by DatumWriter.  Applications should not call.
  public java.lang.Object get(int field$) {
    switch (field$) {
    case 0: return transactionStatus;
    default: throw new org.apache.avro.AvroRuntimeException("Bad index");
    }
  }

  // Used by DatumReader.  Applications should not call.
  @SuppressWarnings(value="unchecked")
  public void put(int field$, java.lang.Object value$) {
    switch (field$) {
    case 0: transactionStatus = (com.opentext.bn.converters.avro.entity.TransactionStatus)value$; break;
    default: throw new org.apache.avro.AvroRuntimeException("Bad index");
    }
  }

  /**
   * Gets the value of the 'transactionStatus' field.
   * @return The value of the 'transactionStatus' field.
   */
  public com.opentext.bn.converters.avro.entity.TransactionStatus getTransactionStatus() {
    return transactionStatus;
  }


  /**
   * Creates a new FinalTaskInfo RecordBuilder.
   * @return A new FinalTaskInfo RecordBuilder
   */
  public static com.opentext.bn.converters.avro.entity.FinalTaskInfo.Builder newBuilder() {
    return new com.opentext.bn.converters.avro.entity.FinalTaskInfo.Builder();
  }

  /**
   * Creates a new FinalTaskInfo RecordBuilder by copying an existing Builder.
   * @param other The existing builder to copy.
   * @return A new FinalTaskInfo RecordBuilder
   */
  public static com.opentext.bn.converters.avro.entity.FinalTaskInfo.Builder newBuilder(com.opentext.bn.converters.avro.entity.FinalTaskInfo.Builder other) {
    return new com.opentext.bn.converters.avro.entity.FinalTaskInfo.Builder(other);
  }

  /**
   * Creates a new FinalTaskInfo RecordBuilder by copying an existing FinalTaskInfo instance.
   * @param other The existing instance to copy.
   * @return A new FinalTaskInfo RecordBuilder
   */
  public static com.opentext.bn.converters.avro.entity.FinalTaskInfo.Builder newBuilder(com.opentext.bn.converters.avro.entity.FinalTaskInfo other) {
    return new com.opentext.bn.converters.avro.entity.FinalTaskInfo.Builder(other);
  }

  /**
   * RecordBuilder for FinalTaskInfo instances.
   */
  public static class Builder extends org.apache.avro.specific.SpecificRecordBuilderBase<FinalTaskInfo>
    implements org.apache.avro.data.RecordBuilder<FinalTaskInfo> {

    private com.opentext.bn.converters.avro.entity.TransactionStatus transactionStatus;

    /** Creates a new Builder */
    private Builder() {
      super(SCHEMA$);
    }

    /**
     * Creates a Builder by copying an existing Builder.
     * @param other The existing Builder to copy.
     */
    private Builder(com.opentext.bn.converters.avro.entity.FinalTaskInfo.Builder other) {
      super(other);
      if (isValidValue(fields()[0], other.transactionStatus)) {
        this.transactionStatus = data().deepCopy(fields()[0].schema(), other.transactionStatus);
        fieldSetFlags()[0] = true;
      }
    }

    /**
     * Creates a Builder by copying an existing FinalTaskInfo instance
     * @param other The existing instance to copy.
     */
    private Builder(com.opentext.bn.converters.avro.entity.FinalTaskInfo other) {
            super(SCHEMA$);
      if (isValidValue(fields()[0], other.transactionStatus)) {
        this.transactionStatus = data().deepCopy(fields()[0].schema(), other.transactionStatus);
        fieldSetFlags()[0] = true;
      }
    }

    /**
      * Gets the value of the 'transactionStatus' field.
      * @return The value.
      */
    public com.opentext.bn.converters.avro.entity.TransactionStatus getTransactionStatus() {
      return transactionStatus;
    }

    /**
      * Sets the value of the 'transactionStatus' field.
      * @param value The value of 'transactionStatus'.
      * @return This builder.
      */
    public com.opentext.bn.converters.avro.entity.FinalTaskInfo.Builder setTransactionStatus(com.opentext.bn.converters.avro.entity.TransactionStatus value) {
      validate(fields()[0], value);
      this.transactionStatus = value;
      fieldSetFlags()[0] = true;
      return this;
    }

    /**
      * Checks whether the 'transactionStatus' field has been set.
      * @return True if the 'transactionStatus' field has been set, false otherwise.
      */
    public boolean hasTransactionStatus() {
      return fieldSetFlags()[0];
    }


    /**
      * Clears the value of the 'transactionStatus' field.
      * @return This builder.
      */
    public com.opentext.bn.converters.avro.entity.FinalTaskInfo.Builder clearTransactionStatus() {
      transactionStatus = null;
      fieldSetFlags()[0] = false;
      return this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public FinalTaskInfo build() {
      try {
        FinalTaskInfo record = new FinalTaskInfo();
        record.transactionStatus = fieldSetFlags()[0] ? this.transactionStatus : (com.opentext.bn.converters.avro.entity.TransactionStatus) defaultValue(fields()[0]);
        return record;
      } catch (java.lang.Exception e) {
        throw new org.apache.avro.AvroRuntimeException(e);
      }
    }
  }

  @SuppressWarnings("unchecked")
  private static final org.apache.avro.io.DatumWriter<FinalTaskInfo>
    WRITER$ = (org.apache.avro.io.DatumWriter<FinalTaskInfo>)MODEL$.createDatumWriter(SCHEMA$);

  @Override public void writeExternal(java.io.ObjectOutput out)
    throws java.io.IOException {
    WRITER$.write(this, SpecificData.getEncoder(out));
  }

  @SuppressWarnings("unchecked")
  private static final org.apache.avro.io.DatumReader<FinalTaskInfo>
    READER$ = (org.apache.avro.io.DatumReader<FinalTaskInfo>)MODEL$.createDatumReader(SCHEMA$);

  @Override public void readExternal(java.io.ObjectInput in)
    throws java.io.IOException {
    READER$.read(this, SpecificData.getDecoder(in));
  }

}
