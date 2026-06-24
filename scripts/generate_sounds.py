"""
Génère 5 sons placeholder simples (synthèse audio basique) pour le mod CACA.
Ce sont des sons FONCTIONNELS mais très simples (bruitages synthétiques) ;
remplacez-les par de vrais enregistrements/sons pour un résultat plus drôle.
"""
import numpy as np
import wave
import os

OUT_DIR = "/home/claude/caca-mod/sounds_wav"
os.makedirs(OUT_DIR, exist_ok=True)

SAMPLE_RATE = 44100


def save_wav(filename, samples, sample_rate=SAMPLE_RATE):
    samples = np.clip(samples, -1.0, 1.0)
    int_samples = (samples * 32767).astype(np.int16)
    with wave.open(filename, "w") as f:
        f.setnchannels(1)
        f.setsampwidth(2)
        f.setframerate(sample_rate)
        f.writeframes(int_samples.tobytes())


def envelope(n, attack=0.05, release=0.3):
    """Enveloppe simple attack/release pour éviter les clics."""
    env = np.ones(n)
    a = int(n * attack)
    r = int(n * release)
    if a > 0:
        env[:a] = np.linspace(0, 1, a)
    if r > 0:
        env[-r:] = np.linspace(1, 0, r)
    return env


def make_fart(duration=0.6):
    """Bruit de pet : oscillation basse fréquence + bruit, modulée en fréquence."""
    t = np.linspace(0, duration, int(SAMPLE_RATE * duration), endpoint=False)
    base_freq = 90 + 30 * np.sin(2 * np.pi * 8 * t)  # vibrato grave
    tone = np.sin(2 * np.pi * base_freq * t)
    noise = np.random.normal(0, 0.25, len(t))
    signal = 0.7 * tone + 0.3 * noise
    signal *= envelope(len(t), attack=0.02, release=0.5)
    return signal


def make_plop(duration=0.25):
    """Son de plop : pop rapide, fréquence descendante."""
    t = np.linspace(0, duration, int(SAMPLE_RATE * duration), endpoint=False)
    freq = np.linspace(600, 120, len(t))
    phase = 2 * np.pi * np.cumsum(freq) / SAMPLE_RATE
    tone = np.sin(phase)
    signal = tone * envelope(len(t), attack=0.01, release=0.7)
    return signal * 0.8


def make_break(duration=0.3):
    """Son de cassage : bruit granuleux court, façon "squish"."""
    t = np.linspace(0, duration, int(SAMPLE_RATE * duration), endpoint=False)
    noise = np.random.normal(0, 0.5, len(t))
    low_tone = 0.3 * np.sin(2 * np.pi * 150 * t)
    signal = noise * 0.6 + low_tone
    signal *= envelope(len(t), attack=0.01, release=0.6)
    return signal * 0.7


def make_eat(duration=0.5):
    """Son de dégustation : suite de petits "miam" grotesques (clics rapprochés)."""
    t = np.linspace(0, duration, int(SAMPLE_RATE * duration), endpoint=False)
    n_chews = 4
    signal = np.zeros(len(t))
    chunk = len(t) // n_chews
    for i in range(n_chews):
        start = i * chunk
        end = start + chunk
        sub_t = t[start:end] - t[start]
        freq = 250 - i * 20
        tone = np.sin(2 * np.pi * freq * sub_t)
        env = envelope(len(sub_t), attack=0.05, release=0.6)
        signal[start:end] = tone * env
    return signal * 0.6


def make_golden_jingle(duration=0.8):
    """Petit jingle doré : arpège ascendant simple, façon "level up"."""
    t_total = np.linspace(0, duration, int(SAMPLE_RATE * duration), endpoint=False)
    signal = np.zeros(len(t_total))
    notes = [523.25, 659.25, 783.99, 1046.50]  # C5 E5 G5 C6
    note_dur = duration / len(notes)
    n_samples_note = int(SAMPLE_RATE * note_dur)
    for i, freq in enumerate(notes):
        t = np.linspace(0, note_dur, n_samples_note, endpoint=False)
        tone = np.sin(2 * np.pi * freq * t)
        env = envelope(len(t), attack=0.05, release=0.5)
        start = i * n_samples_note
        end = start + n_samples_note
        if end > len(signal):
            end = len(signal)
            tone = tone[: end - start]
            env = env[: end - start]
        signal[start:end] += tone * env
    return signal * 0.5


save_wav(os.path.join(OUT_DIR, "caca_fart.wav"), make_fart())
save_wav(os.path.join(OUT_DIR, "caca_plop.wav"), make_plop())
save_wav(os.path.join(OUT_DIR, "caca_break.wav"), make_break())
save_wav(os.path.join(OUT_DIR, "caca_eat.wav"), make_eat())
save_wav(os.path.join(OUT_DIR, "caca_golden_jingle.wav"), make_golden_jingle())

print("Sons WAV générés avec succès.")
