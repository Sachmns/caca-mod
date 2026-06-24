"""
Génère des textures placeholder 16x16 simples pour le mod CACA.
Ce ne sont QUE des placeholders fonctionnels (style "emoji caca" pixelisé) :
remplacez-les par vos propres textures pour un rendu plus poussé.
"""
from PIL import Image, ImageDraw
import os

OUT_ITEM = "/home/claude/caca-mod/src/main/resources/assets/cacamod/textures/item"
OUT_BLOCK = "/home/claude/caca-mod/src/main/resources/assets/cacamod/textures/block"

os.makedirs(OUT_ITEM, exist_ok=True)
os.makedirs(OUT_BLOCK, exist_ok=True)

BROWN_DARK = (92, 64, 38, 255)
BROWN_MID = (121, 85, 50, 255)
BROWN_LIGHT = (150, 107, 64, 255)
TRANSPARENT = (0, 0, 0, 0)

GOLD_DARK = (153, 101, 21, 255)
GOLD_MID = (212, 175, 55, 255)
GOLD_LIGHT = (245, 220, 120, 255)


def draw_swirl_blob(draw, base_dark, base_mid, base_light, size=16):
    """Dessine une forme de tas/spirale simple en pixel art, façon emoji caca."""
    # Fond transparent partout, on dessine une silhouette de "tas à étages"
    # Étage du bas (le plus large)
    draw.ellipse([2, 10, 13, 15], fill=base_dark)
    # Étage du milieu
    draw.ellipse([3, 6, 12, 12], fill=base_mid)
    # Étage du haut (pointe)
    draw.ellipse([5, 2, 10, 8], fill=base_mid)
    # Pointe sommet
    draw.ellipse([6, 0, 9, 4], fill=base_light)
    # Petits reflets clairs
    draw.point((7, 1), fill=base_light)
    draw.point((4, 8), fill=base_light)
    draw.point((10, 11), fill=base_light)
    # Contour légèrement plus sombre pour la lisibilité
    draw.ellipse([2, 10, 13, 15], outline=(40, 28, 16, 255))


def make_item_texture(path, dark, mid, light):
    img = Image.new("RGBA", (16, 16), TRANSPARENT)
    draw = ImageDraw.Draw(img)
    draw_swirl_blob(draw, dark, mid, light)
    img.save(path)


def make_block_texture(path, dark, mid, light):
    # Texture de bloc : motif répétable, sans transparence (bloc plein)
    img = Image.new("RGBA", (16, 16), dark)
    draw = ImageDraw.Draw(img)
    # quelques touches de variation pour casser la monotonie
    for x in range(0, 16, 4):
        for y in range(0, 16, 4):
            draw.rectangle([x, y, x + 1, y + 1], fill=mid)
    draw.ellipse([3, 3, 12, 12], fill=mid)
    draw.ellipse([5, 5, 10, 10], fill=light)
    img.save(path)


make_item_texture(os.path.join(OUT_ITEM, "caca.png"), BROWN_DARK, BROWN_MID, BROWN_LIGHT)
make_item_texture(os.path.join(OUT_ITEM, "caca_dore.png"), GOLD_DARK, GOLD_MID, GOLD_LIGHT)
make_block_texture(os.path.join(OUT_BLOCK, "caca_block.png"), BROWN_DARK, BROWN_MID, BROWN_LIGHT)

print("Textures générées avec succès.")
