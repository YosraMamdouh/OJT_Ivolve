# Lab 10: Node Isolation Using Taints and Labels

A taint is added to a node so that only Pods that tolerate it can be scheduled there, and a label is added to identify the node.

| Setting | Value |
|---------|-------|
| Taint | `workload=ivolveApp:NoSchedule` |
| Label | `project=ivolve` |

> This cluster has a single node (`desktop-1nodl4r`, see [Lab 9](../Lab9)), so the taint and the label were applied to that node. On a cluster with separate worker nodes, the same commands are run against one of the workers.

## Step 1: Taint the node

```bash
kubectl taint nodes desktop-1nodl4r workload=ivolveApp:NoSchedule
```

```
node/desktop-1nodl4r tainted
```

The taint has the form `key=value:effect`. With the effect `NoSchedule`, new Pods are not scheduled on the node unless they have a matching toleration. Pods that are already running are not affected.

## Step 2: Label the node

```bash
kubectl label nodes desktop-1nodl4r project=ivolve
```

```
node/desktop-1nodl4r labeled
```

## Step 3: Verify

**The taint:**

```bash
kubectl describe node desktop-1nodl4r | grep -i -A 2 Taints
```

```
Taints:             workload=ivolveApp:NoSchedule
Unschedulable:      false
Lease:
```

**The label:**

```bash
kubectl get nodes --show-labels
```

```
NAME              STATUS   ROLES                  AGE   VERSION   LABELS
desktop-1nodl4r   Ready    control-plane,worker   76m   v1.36.1   beta.kubernetes.io/arch=amd64,beta.kubernetes.io/os=linux,kubernetes.io/arch=amd64,kubernetes.io/hostname=desktop-1nodl4r,kubernetes.io/os=linux,node-role.kubernetes.io/control-plane=,node-role.kubernetes.io/worker=worker,node.kubernetes.io/exclude-from-external-load-balancers=,project=ivolve
```

The output contains the label `project=ivolve`, and `describe` shows the taint `workload=ivolveApp:NoSchedule`.

## How to use them

- A Pod that must run on the isolated node needs a toleration for the taint:

  ```yaml
  tolerations:
    - key: "workload"
      operator: "Equal"
      value: "ivolveApp"
      effect: "NoSchedule"
  ```

- A `nodeSelector` with the label (`project: ivolve`) makes the Pod choose this node. The taint keeps other Pods away, and the label attracts the right ones.

## Remove the taint

```bash
kubectl taint nodes desktop-1nodl4r workload=ivolveApp:NoSchedule-
```

## Screenshots

See the [`screenshots/`](./screenshots) folder.
