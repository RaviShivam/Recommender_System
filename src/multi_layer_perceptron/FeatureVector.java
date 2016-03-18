package multi_layer_perceptron;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class FeatureVector extends ArrayList<Double> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * Label of the feature.
	 */
	List<Double> label;

	/**
	 * Constructor
	 * @param l The label of this feature vector.
	 */
	public FeatureVector(List<Double> l) {
		label = l;
	}

	/**
	 * @return Returns the label.
	 */
	public List<Double> getLabel() {
		return label;
	}

	/**
	 * Calculates the product of this feature vector with vector weights.
	 * @param weights The vector with which the product is calculated.
	 * @return The product of the two vectors.
	 */
	public double product(List<Double> weights) {
		assert(weights.size() == size());
        double result = 0.0;
		for(int i=0;i<size();i++){
    		result += weights.get(i)*this.get(i);
		}
		return result;
	}

	/**
	 * Calculates the (Euclidean) distance between the given vector and this feature vector.
	 * @param vector The vector to calculate the distance to.
	 * @return The distance to vector.
	 */
	public double distance(List<Double> vector) {
		assert(vector.size() == size());
        List<Double> norm = IntStream.range(0,size()).mapToObj(i -> Math.abs(this.get(i) - vector.get(i))).collect(Collectors.toList());
		List<Double> normpow = norm.stream().map(i -> i*i).collect(Collectors.toList());
		double result = Math.sqrt(normpow.stream().mapToDouble(Double::doubleValue).sum());
        return result;
	}

	/**
	 * Converts this object to a String object.
	 */
	@Override
	public String toString() {
		return "<" + label + ", " + super.toString() + ">";
	}
}
