import tensorflow as tf
from tensorflow.keras import layers, models

def build_lstm_autoencoder(sequence_length=20, feature_count=4):
    """
    Builds an LSTM Autoencoder for time-series anomaly detection.
    Input: (timesteps, features) -> (20, 4) [lat, lon, sog, cog]
    """
    model = models.Sequential([
        # Encoder
        layers.Input(shape=(sequence_length, feature_count)),
        layers.LSTM(32, activation='relu', return_sequences=True),
        layers.LSTM(16, activation='relu', return_sequences=False),
        
        # Repetition of the latent vector
        layers.RepeatVector(sequence_length),
        
        # Decoder
        layers.LSTM(16, activation='relu', return_sequences=True),
        layers.LSTM(32, activation='relu', return_sequences=True),
        layers.TimeDistributed(layers.Dense(feature_count))
    ])
    
    model.compile(optimizer='adam', loss='mae')
    return model

class AnomalyScorer:
    def __init__(self, model_path=None):
        if model_path and os.path.exists(model_path):
            self.model = tf.keras.models.load_model(model_path)
        else:
            self.model = build_lstm_autoencoder()
            print("Warning: Initializing with UNTRAINED LSTM model.")

    def get_score(self, sequence):
        """
        Calculates the anomaly score (Reconstruction Error).
        sequence: numpy array of shape (1, 20, 4)
        """
        reconstruction = self.model.predict(sequence, verbose=0)
        # Mean Absolute Error as Anomaly Score
        score = tf.reduce_mean(tf.abs(sequence - reconstruction)).numpy()
        return float(score)

if __name__ == "__main__":
    # Test the model structure
    model = build_lstm_autoencoder()
    model.summary()
