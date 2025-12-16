# Username Checker Helm Chart

This Helm chart deploys the Username Checker application stack, including:
- **Username Service**: Spring Boot application.
- **Cassandra**: NoSQL database (StatefulSet).
- **Redis**: Caching layer (Deployment).

## Prerequisites

- Kubernetes 1.19+
- Helm 3.0+

## Installing the Chart

To install the chart with the release name `my-release`:

```console
$ helm install my-release ./username-checker
```

## Configuration

The following table lists the configurable parameters of the Username Checker chart and their default values.

| Parameter | Description | Default |
|-----------|-------------|---------|
| `cassandra.replicaCount` | Number of Cassandra nodes | `4` |
| `cassandra.persistence.size` | Size of Cassandra PVC | `1Gi` |
| `redis.replicaCount` | Number of Redis replicas | `1` |
| `usernameService.image.repository` | Image repository | `sidboy/username-service` |
| `usernameService.service.type` | Service type | `LoadBalancer` |

Specify each parameter using the `--set key=value[,key=value]` argument to `helm install`.

## Architecture details

The chart creates:
- A ConfigMap `app-config` for shared configuration.
- A StatefulSet for Cassandra with a headless service.
- A Job `cassandra-init` that initializes the keyspace and tables.
- A Deployment for Redis.
- A Deployment for Username Service with HPA enabled.
