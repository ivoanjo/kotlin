// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: compiler/ir/serialization.common/src/KotlinIr.proto

package org.jetbrains.kotlin.backend.common.serialization.proto;

public interface IrExpressionOrBuilder extends
    // @@protoc_insertion_point(interface_extends:org.jetbrains.kotlin.backend.common.serialization.proto.IrExpression)
    org.jetbrains.kotlin.protobuf.MessageLiteOrBuilder {

  /**
   * <code>required .org.jetbrains.kotlin.backend.common.serialization.proto.IrOperation operation = 1;</code>
   */
  boolean hasOperation();
  /**
   * <code>required .org.jetbrains.kotlin.backend.common.serialization.proto.IrOperation operation = 1;</code>
   */
  org.jetbrains.kotlin.backend.common.serialization.proto.IrOperation getOperation();

  /**
   * <code>required int32 type = 2;</code>
   */
  boolean hasType();
  /**
   * <code>required int32 type = 2;</code>
   */
  int getType();

  /**
   * <code>required int64 coordinates = 3;</code>
   */
  boolean hasCoordinates();
  /**
   * <code>required int64 coordinates = 3;</code>
   */
  long getCoordinates();
}