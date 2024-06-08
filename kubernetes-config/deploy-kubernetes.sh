
kubectl apply -f namespace.yaml
kubectl apply -n finamer -f secrets/
kubectl apply -n finamer -f deployments/
kubectl apply -n finamer -f services/
