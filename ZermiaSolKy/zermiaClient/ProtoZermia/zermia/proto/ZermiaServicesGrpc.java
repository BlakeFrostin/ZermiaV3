package zermia.proto;

import static io.grpc.stub.ClientCalls.asyncUnaryCall;
import static io.grpc.stub.ClientCalls.asyncServerStreamingCall;
import static io.grpc.stub.ClientCalls.asyncClientStreamingCall;
import static io.grpc.stub.ClientCalls.asyncBidiStreamingCall;
import static io.grpc.stub.ClientCalls.blockingUnaryCall;
import static io.grpc.stub.ClientCalls.blockingServerStreamingCall;
import static io.grpc.stub.ClientCalls.futureUnaryCall;
import static io.grpc.MethodDescriptor.generateFullMethodName;
import static io.grpc.stub.ServerCalls.asyncUnaryCall;
import static io.grpc.stub.ServerCalls.asyncServerStreamingCall;
import static io.grpc.stub.ServerCalls.asyncClientStreamingCall;
import static io.grpc.stub.ServerCalls.asyncBidiStreamingCall;
import static io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall;
import static io.grpc.stub.ServerCalls.asyncUnimplementedStreamingCall;

/**
 */
@javax.annotation.Generated(
    value = "by gRPC proto compiler (version 1.0.1)",
    comments = "Source: ProtoRuntime.proto")
public class ZermiaServicesGrpc {

  private ZermiaServicesGrpc() {}

  public static final String SERVICE_NAME = "ZermiaServices";

  // Static method descriptors that strictly reflect the proto.
  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  public static final io.grpc.MethodDescriptor<zermia.proto.ProtoRuntime.ConnectionRequest,
      zermia.proto.ProtoRuntime.ConnectionReply> METHOD_FIRST_CONNECTION =
      io.grpc.MethodDescriptor.create(
          io.grpc.MethodDescriptor.MethodType.UNARY,
          generateFullMethodName(
              "ZermiaServices", "FirstConnection"),
          io.grpc.protobuf.ProtoUtils.marshaller(zermia.proto.ProtoRuntime.ConnectionRequest.getDefaultInstance()),
          io.grpc.protobuf.ProtoUtils.marshaller(zermia.proto.ProtoRuntime.ConnectionReply.getDefaultInstance()));
  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  public static final io.grpc.MethodDescriptor<zermia.proto.ProtoRuntime.ConnectionSecondRequest,
      zermia.proto.ProtoRuntime.ConnectionSecondReply> METHOD_SECOND_CONNECTION =
      io.grpc.MethodDescriptor.create(
          io.grpc.MethodDescriptor.MethodType.UNARY,
          generateFullMethodName(
              "ZermiaServices", "SecondConnection"),
          io.grpc.protobuf.ProtoUtils.marshaller(zermia.proto.ProtoRuntime.ConnectionSecondRequest.getDefaultInstance()),
          io.grpc.protobuf.ProtoUtils.marshaller(zermia.proto.ProtoRuntime.ConnectionSecondReply.getDefaultInstance()));
  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  public static final io.grpc.MethodDescriptor<zermia.proto.ProtoRuntime.FaultActivationRequest,
      zermia.proto.ProtoRuntime.FaultActivationReply> METHOD_FAULT_SERVICE =
      io.grpc.MethodDescriptor.create(
          io.grpc.MethodDescriptor.MethodType.UNARY,
          generateFullMethodName(
              "ZermiaServices", "FaultService"),
          io.grpc.protobuf.ProtoUtils.marshaller(zermia.proto.ProtoRuntime.FaultActivationRequest.getDefaultInstance()),
          io.grpc.protobuf.ProtoUtils.marshaller(zermia.proto.ProtoRuntime.FaultActivationReply.getDefaultInstance()));
  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  public static final io.grpc.MethodDescriptor<zermia.proto.ProtoRuntime.StatsRequest,
      zermia.proto.ProtoRuntime.StatsReply> METHOD_STATS_SERVICE =
      io.grpc.MethodDescriptor.create(
          io.grpc.MethodDescriptor.MethodType.UNARY,
          generateFullMethodName(
              "ZermiaServices", "StatsService"),
          io.grpc.protobuf.ProtoUtils.marshaller(zermia.proto.ProtoRuntime.StatsRequest.getDefaultInstance()),
          io.grpc.protobuf.ProtoUtils.marshaller(zermia.proto.ProtoRuntime.StatsReply.getDefaultInstance()));
  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  public static final io.grpc.MethodDescriptor<zermia.proto.ProtoRuntime.Stat1SecRequest,
      zermia.proto.ProtoRuntime.Stat1SecReply> METHOD_STAT1SEC_SERVICE =
      io.grpc.MethodDescriptor.create(
          io.grpc.MethodDescriptor.MethodType.UNARY,
          generateFullMethodName(
              "ZermiaServices", "Stat1SecService"),
          io.grpc.protobuf.ProtoUtils.marshaller(zermia.proto.ProtoRuntime.Stat1SecRequest.getDefaultInstance()),
          io.grpc.protobuf.ProtoUtils.marshaller(zermia.proto.ProtoRuntime.Stat1SecReply.getDefaultInstance()));

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static ZermiaServicesStub newStub(io.grpc.Channel channel) {
    return new ZermiaServicesStub(channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static ZermiaServicesBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    return new ZermiaServicesBlockingStub(channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary and streaming output calls on the service
   */
  public static ZermiaServicesFutureStub newFutureStub(
      io.grpc.Channel channel) {
    return new ZermiaServicesFutureStub(channel);
  }

  /**
   */
  public static abstract class ZermiaServicesImplBase implements io.grpc.BindableService {

    /**
     */
    public void firstConnection(zermia.proto.ProtoRuntime.ConnectionRequest request,
        io.grpc.stub.StreamObserver<zermia.proto.ProtoRuntime.ConnectionReply> responseObserver) {
      asyncUnimplementedUnaryCall(METHOD_FIRST_CONNECTION, responseObserver);
    }

    /**
     */
    public void secondConnection(zermia.proto.ProtoRuntime.ConnectionSecondRequest request,
        io.grpc.stub.StreamObserver<zermia.proto.ProtoRuntime.ConnectionSecondReply> responseObserver) {
      asyncUnimplementedUnaryCall(METHOD_SECOND_CONNECTION, responseObserver);
    }

    /**
     */
    public void faultService(zermia.proto.ProtoRuntime.FaultActivationRequest request,
        io.grpc.stub.StreamObserver<zermia.proto.ProtoRuntime.FaultActivationReply> responseObserver) {
      asyncUnimplementedUnaryCall(METHOD_FAULT_SERVICE, responseObserver);
    }

    /**
     */
    public void statsService(zermia.proto.ProtoRuntime.StatsRequest request,
        io.grpc.stub.StreamObserver<zermia.proto.ProtoRuntime.StatsReply> responseObserver) {
      asyncUnimplementedUnaryCall(METHOD_STATS_SERVICE, responseObserver);
    }

    /**
     */
    public void stat1SecService(zermia.proto.ProtoRuntime.Stat1SecRequest request,
        io.grpc.stub.StreamObserver<zermia.proto.ProtoRuntime.Stat1SecReply> responseObserver) {
      asyncUnimplementedUnaryCall(METHOD_STAT1SEC_SERVICE, responseObserver);
    }

    @java.lang.Override public io.grpc.ServerServiceDefinition bindService() {
      return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
          .addMethod(
            METHOD_FIRST_CONNECTION,
            asyncUnaryCall(
              new MethodHandlers<
                zermia.proto.ProtoRuntime.ConnectionRequest,
                zermia.proto.ProtoRuntime.ConnectionReply>(
                  this, METHODID_FIRST_CONNECTION)))
          .addMethod(
            METHOD_SECOND_CONNECTION,
            asyncUnaryCall(
              new MethodHandlers<
                zermia.proto.ProtoRuntime.ConnectionSecondRequest,
                zermia.proto.ProtoRuntime.ConnectionSecondReply>(
                  this, METHODID_SECOND_CONNECTION)))
          .addMethod(
            METHOD_FAULT_SERVICE,
            asyncUnaryCall(
              new MethodHandlers<
                zermia.proto.ProtoRuntime.FaultActivationRequest,
                zermia.proto.ProtoRuntime.FaultActivationReply>(
                  this, METHODID_FAULT_SERVICE)))
          .addMethod(
            METHOD_STATS_SERVICE,
            asyncUnaryCall(
              new MethodHandlers<
                zermia.proto.ProtoRuntime.StatsRequest,
                zermia.proto.ProtoRuntime.StatsReply>(
                  this, METHODID_STATS_SERVICE)))
          .addMethod(
            METHOD_STAT1SEC_SERVICE,
            asyncUnaryCall(
              new MethodHandlers<
                zermia.proto.ProtoRuntime.Stat1SecRequest,
                zermia.proto.ProtoRuntime.Stat1SecReply>(
                  this, METHODID_STAT1SEC_SERVICE)))
          .build();
    }
  }

  /**
   */
  public static final class ZermiaServicesStub extends io.grpc.stub.AbstractStub<ZermiaServicesStub> {
    private ZermiaServicesStub(io.grpc.Channel channel) {
      super(channel);
    }

    private ZermiaServicesStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected ZermiaServicesStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new ZermiaServicesStub(channel, callOptions);
    }

    /**
     */
    public void firstConnection(zermia.proto.ProtoRuntime.ConnectionRequest request,
        io.grpc.stub.StreamObserver<zermia.proto.ProtoRuntime.ConnectionReply> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(METHOD_FIRST_CONNECTION, getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void secondConnection(zermia.proto.ProtoRuntime.ConnectionSecondRequest request,
        io.grpc.stub.StreamObserver<zermia.proto.ProtoRuntime.ConnectionSecondReply> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(METHOD_SECOND_CONNECTION, getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void faultService(zermia.proto.ProtoRuntime.FaultActivationRequest request,
        io.grpc.stub.StreamObserver<zermia.proto.ProtoRuntime.FaultActivationReply> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(METHOD_FAULT_SERVICE, getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void statsService(zermia.proto.ProtoRuntime.StatsRequest request,
        io.grpc.stub.StreamObserver<zermia.proto.ProtoRuntime.StatsReply> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(METHOD_STATS_SERVICE, getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void stat1SecService(zermia.proto.ProtoRuntime.Stat1SecRequest request,
        io.grpc.stub.StreamObserver<zermia.proto.ProtoRuntime.Stat1SecReply> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(METHOD_STAT1SEC_SERVICE, getCallOptions()), request, responseObserver);
    }
  }

  /**
   */
  public static final class ZermiaServicesBlockingStub extends io.grpc.stub.AbstractStub<ZermiaServicesBlockingStub> {
    private ZermiaServicesBlockingStub(io.grpc.Channel channel) {
      super(channel);
    }

    private ZermiaServicesBlockingStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected ZermiaServicesBlockingStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new ZermiaServicesBlockingStub(channel, callOptions);
    }

    /**
     */
    public zermia.proto.ProtoRuntime.ConnectionReply firstConnection(zermia.proto.ProtoRuntime.ConnectionRequest request) {
      return blockingUnaryCall(
          getChannel(), METHOD_FIRST_CONNECTION, getCallOptions(), request);
    }

    /**
     */
    public zermia.proto.ProtoRuntime.ConnectionSecondReply secondConnection(zermia.proto.ProtoRuntime.ConnectionSecondRequest request) {
      return blockingUnaryCall(
          getChannel(), METHOD_SECOND_CONNECTION, getCallOptions(), request);
    }

    /**
     */
    public zermia.proto.ProtoRuntime.FaultActivationReply faultService(zermia.proto.ProtoRuntime.FaultActivationRequest request) {
      return blockingUnaryCall(
          getChannel(), METHOD_FAULT_SERVICE, getCallOptions(), request);
    }

    /**
     */
    public zermia.proto.ProtoRuntime.StatsReply statsService(zermia.proto.ProtoRuntime.StatsRequest request) {
      return blockingUnaryCall(
          getChannel(), METHOD_STATS_SERVICE, getCallOptions(), request);
    }

    /**
     */
    public zermia.proto.ProtoRuntime.Stat1SecReply stat1SecService(zermia.proto.ProtoRuntime.Stat1SecRequest request) {
      return blockingUnaryCall(
          getChannel(), METHOD_STAT1SEC_SERVICE, getCallOptions(), request);
    }
  }

  /**
   */
  public static final class ZermiaServicesFutureStub extends io.grpc.stub.AbstractStub<ZermiaServicesFutureStub> {
    private ZermiaServicesFutureStub(io.grpc.Channel channel) {
      super(channel);
    }

    private ZermiaServicesFutureStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected ZermiaServicesFutureStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new ZermiaServicesFutureStub(channel, callOptions);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<zermia.proto.ProtoRuntime.ConnectionReply> firstConnection(
        zermia.proto.ProtoRuntime.ConnectionRequest request) {
      return futureUnaryCall(
          getChannel().newCall(METHOD_FIRST_CONNECTION, getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<zermia.proto.ProtoRuntime.ConnectionSecondReply> secondConnection(
        zermia.proto.ProtoRuntime.ConnectionSecondRequest request) {
      return futureUnaryCall(
          getChannel().newCall(METHOD_SECOND_CONNECTION, getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<zermia.proto.ProtoRuntime.FaultActivationReply> faultService(
        zermia.proto.ProtoRuntime.FaultActivationRequest request) {
      return futureUnaryCall(
          getChannel().newCall(METHOD_FAULT_SERVICE, getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<zermia.proto.ProtoRuntime.StatsReply> statsService(
        zermia.proto.ProtoRuntime.StatsRequest request) {
      return futureUnaryCall(
          getChannel().newCall(METHOD_STATS_SERVICE, getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<zermia.proto.ProtoRuntime.Stat1SecReply> stat1SecService(
        zermia.proto.ProtoRuntime.Stat1SecRequest request) {
      return futureUnaryCall(
          getChannel().newCall(METHOD_STAT1SEC_SERVICE, getCallOptions()), request);
    }
  }

  private static final int METHODID_FIRST_CONNECTION = 0;
  private static final int METHODID_SECOND_CONNECTION = 1;
  private static final int METHODID_FAULT_SERVICE = 2;
  private static final int METHODID_STATS_SERVICE = 3;
  private static final int METHODID_STAT1SEC_SERVICE = 4;

  private static class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final ZermiaServicesImplBase serviceImpl;
    private final int methodId;

    public MethodHandlers(ZermiaServicesImplBase serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_FIRST_CONNECTION:
          serviceImpl.firstConnection((zermia.proto.ProtoRuntime.ConnectionRequest) request,
              (io.grpc.stub.StreamObserver<zermia.proto.ProtoRuntime.ConnectionReply>) responseObserver);
          break;
        case METHODID_SECOND_CONNECTION:
          serviceImpl.secondConnection((zermia.proto.ProtoRuntime.ConnectionSecondRequest) request,
              (io.grpc.stub.StreamObserver<zermia.proto.ProtoRuntime.ConnectionSecondReply>) responseObserver);
          break;
        case METHODID_FAULT_SERVICE:
          serviceImpl.faultService((zermia.proto.ProtoRuntime.FaultActivationRequest) request,
              (io.grpc.stub.StreamObserver<zermia.proto.ProtoRuntime.FaultActivationReply>) responseObserver);
          break;
        case METHODID_STATS_SERVICE:
          serviceImpl.statsService((zermia.proto.ProtoRuntime.StatsRequest) request,
              (io.grpc.stub.StreamObserver<zermia.proto.ProtoRuntime.StatsReply>) responseObserver);
          break;
        case METHODID_STAT1SEC_SERVICE:
          serviceImpl.stat1SecService((zermia.proto.ProtoRuntime.Stat1SecRequest) request,
              (io.grpc.stub.StreamObserver<zermia.proto.ProtoRuntime.Stat1SecReply>) responseObserver);
          break;
        default:
          throw new AssertionError();
      }
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public io.grpc.stub.StreamObserver<Req> invoke(
        io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        default:
          throw new AssertionError();
      }
    }
  }

  public static io.grpc.ServiceDescriptor getServiceDescriptor() {
    return new io.grpc.ServiceDescriptor(SERVICE_NAME,
        METHOD_FIRST_CONNECTION,
        METHOD_SECOND_CONNECTION,
        METHOD_FAULT_SERVICE,
        METHOD_STATS_SERVICE,
        METHOD_STAT1SEC_SERVICE);
  }

}
