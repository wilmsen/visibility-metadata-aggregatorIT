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
public class RelatedTransaction extends org.apache.avro.specific.SpecificRecordBase implements org.apache.avro.specific.SpecificRecord {
  private static final long serialVersionUID = 6937126203931115863L;
  public static final org.apache.avro.Schema SCHEMA$ = new org.apache.avro.Schema.Parser().parse("{\"type\":\"record\",\"name\":\"RelatedTransaction\",\"namespace\":\"com.opentext.bn.converters.avro.entity\",\"fields\":[{\"name\":\"relationType\",\"type\":{\"type\":\"string\",\"avro.java.string\":\"String\"}},{\"name\":\"transactionId\",\"type\":{\"type\":\"string\",\"avro.java.string\":\"String\"}}]}");
  public static org.apache.avro.Schema getClassSchema() { return SCHEMA$; }

  private static SpecificData MODEL$ = new SpecificData();

  private static final BinaryMessageEncoder<RelatedTransaction> ENCODER =
      new BinaryMessageEncoder<RelatedTransaction>(MODEL$, SCHEMA$);

  private static final BinaryMessageDecoder<RelatedTransaction> DECODER =
      new BinaryMessageDecoder<RelatedTransaction>(MODEL$, SCHEMA$);

  /**
   * Return the BinaryMessageDecoder instance used by this class.
   */
  public static BinaryMessageDecoder<RelatedTransaction> getDecoder() {
    return DECODER;
  }

  /**
   * Create a new BinaryMessageDecoder instance for this class that uses the specified {@link SchemaStore}.
   * @param resolver a {@link SchemaStore} used to find schemas by fingerprint
   */
  public static BinaryMessageDecoder<RelatedTransaction> createDecoder(SchemaStore resolver) {
    return new BinaryMessageDecoder<RelatedTransaction>(MODEL$, SCHEMA$, resolver);
  }

  /** Serializes this RelatedTransaction to a ByteBuffer. */
  public java.nio.ByteBuffer toByteBuffer() throws java.io.IOException {
    return ENCODER.encode(this);
  }

  /** Deserializes a RelatedTransaction from a ByteBuffer. */
  public static RelatedTransaction fromByteBuffer(
      java.nio.ByteBuffer b) throws java.io.IOException {
    return DECODER.decode(b);
  }

   private java.lang.String relationType;
   private java.lang.String transactionId;

  /**
   * Default constructor.  Note that this does not initialize fields
   * to their default values from the schema.  If that is desired then
   * one should use <code>newBuilder()</code>.
   */
  public RelatedTransaction() {}

  /**
   * All-args constructor.
   * @param relationType The new value for relationType
   * @param transactionId The new value for transactionId
   */
  public RelatedTransaction(java.lang.String relationType, java.lang.String transactionId) {
    this.relationType = relationType;
    this.transactionId = transactionId;
  }

  public org.apache.avro.Schema getSchema() { return SCHEMA$; }
  // Used by DatumWriter.  Applications should not call.
  public java.lang.Object get(int field$) {
    switch (field$) {
    case 0: return relationType;
    case 1: return transactionId;
    default: throw new org.apache.avro.AvroRuntimeException("Bad index");
    }
  }

  // Used by DatumReader.  Applications should not call.
  @SuppressWarnings(value="unchecked")
  public void put(int field$, java.lang.Object value$) {
    switch (field$) {
    case 0: relationType = (java.lang.String)value$; break;
    case 1: transactionId = (java.lang.String)value$; break;
    default: throw new org.apache.avro.AvroRuntimeException("Bad index");
    }
  }

  /**
   * Gets the value of the 'relationType' field.
   * @return The value of the 'relationType' field.
   */
  public java.lang.String getRelationType() {
    return relationType;
  }


  /**
   * Gets the value of the 'transactionId' field.
   * @return The value of the 'transactionId' field.
   */
  public java.lang.String getTransactionId() {
    return transactionId;
  }


  /**
   * Creates a new RelatedTransaction RecordBuilder.
   * @return A new RelatedTransaction RecordBuilder
   */
  public static com.opentext.bn.converters.avro.entity.RelatedTransaction.Builder newBuilder() {
    return new com.opentext.bn.converters.avro.entity.RelatedTransaction.Builder();
  }

  /**
   * Creates a new RelatedTransaction RecordBuilder by copying an existing Builder.
   * @param other The existing builder to copy.
   * @return A new RelatedTransaction RecordBuilder
   */
  public static com.opentext.bn.converters.avro.entity.RelatedTransaction.Builder newBuilder(com.opentext.bn.converters.avro.entity.RelatedTransaction.Builder other) {
    return new com.opentext.bn.converters.avro.entity.RelatedTransaction.Builder(other);
  }

  /**
   * Creates a new RelatedTransaction RecordBuilder by copying an existing RelatedTransaction instance.
   * @param other The existing instance to copy.
   * @return A new RelatedTransaction RecordBuilder
   */
  public static com.opentext.bn.converters.avro.entity.RelatedTransaction.Builder newBuilder(com.opentext.bn.converters.avro.entity.RelatedTransaction other) {
    return new com.opentext.bn.converters.avro.entity.RelatedTransaction.Builder(other);
  }

  /**
   * RecordBuilder for RelatedTransaction instances.
   */
  public static class Builder extends org.apache.avro.specific.SpecificRecordBuilderBase<RelatedTransaction>
    implements org.apache.avro.data.RecordBuilder<RelatedTransaction> {

    private java.lang.String relationType;
    private java.lang.String transactionId;

    /** Creates a new Builder */
    private Builder() {
      super(SCHEMA$);
    }

    /**
     * Creates a Builder by copying an existing Builder.
     * @param other The existing Builder to copy.
     */
    private Builder(com.opentext.bn.converters.avro.entity.RelatedTransaction.Builder other) {
      super(other);
      if (isValidValue(fields()[0], other.relationType)) {
        this.relationType = data().deepCopy(fields()[0].schema(), other.relationType);
        fieldSetFlags()[0] = true;
      }
      if (isValidValue(fields()[1], other.transactionId)) {
        this.transactionId = data().deepCopy(fields()[1].schema(), other.transactionId);
        fieldSetFlags()[1] = true;
      }
    }

    /**
     * Creates a Builder by copying an existing RelatedTransaction instance
     * @param other The existing instance to copy.
     */
    private Builder(com.opentext.bn.converters.avro.entity.RelatedTransaction other) {
            super(SCHEMA$);
      if (isValidValue(fields()[0], other.relationType)) {
        this.relationType = data().deepCopy(fields()[0].schema(), other.relationType);
        fieldSetFlags()[0] = true;
      }
      if (isValidValue(fields()[1], other.transactionId)) {
        this.transactionId = data().deepCopy(fields()[1].schema(), other.transactionId);
        fieldSetFlags()[1] = true;
      }
    }

    /**
      * Gets the value of the 'relationType' field.
      * @return The value.
      */
    public java.lang.String getRelationType() {
      return relationType;
    }

    /**
      * Sets the value of the 'relationType' field.
      * @param value The value of 'relationType'.
      * @return This builder.
      */
    public com.opentext.bn.converters.avro.entity.RelatedTransaction.Builder setRelationType(java.lang.String value) {
      validate(fields()[0], value);
      this.relationType = value;
      fieldSetFlags()[0] = true;
      return this;
    }

    /**
      * Checks whether the 'relationType' field has been set.
      * @return True if the 'relationType' field has been set, false otherwise.
      */
    public boolean hasRelationType() {
      return fieldSetFlags()[0];
    }


    /**
      * Clears the value of the 'relationType' field.
      * @return This builder.
      */
    public com.opentext.bn.converters.avro.entity.RelatedTransaction.Builder clearRelationType() {
      relationType = null;
      fieldSetFlags()[0] = false;
      return this;
    }

    /**
      * Gets the value of the 'transactionId' field.
      * @return The value.
      */
    public java.lang.String getTransactionId() {
      return transactionId;
    }

    /**
      * Sets the value of the 'transactionId' field.
      * @param value The value of 'transactionId'.
      * @return This builder.
      */
    public com.opentext.bn.converters.avro.entity.RelatedTransaction.Builder setTransactionId(java.lang.String value) {
      validate(fields()[1], value);
      this.transactionId = value;
      fieldSetFlags()[1] = true;
      return this;
    }

    /**
      * Checks whether the 'transactionId' field has been set.
      * @return True if the 'transactionId' field has been set, false otherwise.
      */
    public boolean hasTransactionId() {
      return fieldSetFlags()[1];
    }


    /**
      * Clears the value of the 'transactionId' field.
      * @return This builder.
      */
    public com.opentext.bn.converters.avro.entity.RelatedTransaction.Builder clearTransactionId() {
      transactionId = null;
      fieldSetFlags()[1] = false;
      return this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public RelatedTransaction build() {
      try {
        RelatedTransaction record = new RelatedTransaction();
        record.relationType = fieldSetFlags()[0] ? this.relationType : (java.lang.String) defaultValue(fields()[0]);
        record.transactionId = fieldSetFlags()[1] ? this.transactionId : (java.lang.String) defaultValue(fields()[1]);
        return record;
      } catch (java.lang.Exception e) {
        throw new org.apache.avro.AvroRuntimeException(e);
      }
    }
  }

  @SuppressWarnings("unchecked")
  private static final org.apache.avro.io.DatumWriter<RelatedTransaction>
    WRITER$ = (org.apache.avro.io.DatumWriter<RelatedTransaction>)MODEL$.createDatumWriter(SCHEMA$);

  @Override public void writeExternal(java.io.ObjectOutput out)
    throws java.io.IOException {
    WRITER$.write(this, SpecificData.getEncoder(out));
  }

  @SuppressWarnings("unchecked")
  private static final org.apache.avro.io.DatumReader<RelatedTransaction>
    READER$ = (org.apache.avro.io.DatumReader<RelatedTransaction>)MODEL$.createDatumReader(SCHEMA$);

  @Override public void readExternal(java.io.ObjectInput in)
    throws java.io.IOException {
    READER$.read(this, SpecificData.getDecoder(in));
  }

}
