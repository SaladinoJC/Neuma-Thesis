"""Adapt the user-supplied Neuma SVGs to Android vectors; keep originals intact.

Run from any directory. PNGs are copied byte-for-byte, without image processing.
Brand colours come from design-tokens.json; no external dependencies or downloads.
"""
from pathlib import Path
import json
import re
import shutil
from urllib.parse import quote
import xml.etree.ElementTree as ET

ROOT = Path(__file__).resolve().parents[2]
SOURCE = ROOT / 'docs/logo/assets'
RES = ROOT / 'Plataforma/app/src/main/res'
BRAND = json.loads((ROOT / 'design-tokens.json').read_text(encoding='utf-8'))['brand']


def write(relative, text):
    target = RES / relative
    target.parent.mkdir(parents=True, exist_ok=True)
    target.write_text(text + '\n', encoding='utf-8')


def vector(name, size=256, transform=None, background=False, colour='@color/neuma_forest'):
    # The supplied Clivis master has a stroked path and a rounded rectangle.
    svg = ET.parse(SOURCE / 'neuma-symbol-clivis.svg').getroot()
    ns = {'svg': 'http://www.w3.org/2000/svg'}
    path = svg.find('svg:path', ns)
    rect = svg.find('svg:rect', ns)
    x, y, w, h, r = (float(rect.attrib[k]) for k in ('x', 'y', 'width', 'height', 'rx'))
    rectangle = (f'M{x+r},{y} H{x+w-r} Q{x+w},{y} {x+w},{y+r} '
                 f'V{y+h-r} Q{x+w},{y+h} {x+w-r},{y+h} '
                 f'H{x+r} Q{x},{y+h} {x},{y+h-r} V{y+r} Q{x},{y} {x+r},{y} Z')
    parts = [f'<vector xmlns:android="http://schemas.android.com/apk/res/android" android:width="108dp" android:height="108dp" android:viewportWidth="{size}" android:viewportHeight="{size}">']
    if background:
        parts.append(f'    <path android:fillColor="@color/neuma_forest" android:pathData="M0,0 H{size} V{size} H0 Z" />')
    if transform:
        tx, ty, scale = transform
        parts.append(f'    <group android:translateX="{tx}" android:translateY="{ty}" android:scaleX="{scale}" android:scaleY="{scale}">')
    parts.extend([
        f'    <path android:fillColor="@android:color/transparent" android:strokeColor="{colour}" android:strokeWidth="{path.attrib["stroke-width"]}" android:strokeLineCap="round" android:strokeLineJoin="round" android:pathData="{path.attrib["d"]}" />',
        f'    <path android:fillColor="{colour}" android:pathData="{rectangle}" />',
    ])
    if transform:
        parts.append('    </group>')
    parts.append('</vector>')
    write('drawable/' + name + '.xml', '\n'.join(parts))


write('values/neuma_brand.xml', f'''<resources>
    <color name="neuma_forest">{BRAND['forest']}</color>
    <color name="neuma_ivory">{BRAND['ivory']}</color>
</resources>''')
vector('neuma_symbol')
# Legacy icon: exactly the geometry/colours of the supplied dark icon.
vector('ic_launcher', 1024, (100, 150, 3.28), True, '@color/neuma_ivory')
# Adaptive icon: supplied transparent foreground transform, ivory on forest.
vector('neuma_launcher_foreground', 1024, (210, 255, 2.4), colour='@color/neuma_ivory')
vector('neuma_launcher_monochrome', 1024, (210, 255, 2.4), colour='@android:color/white')
# Android 12's central splash mask needs breathing room around the symbol.
vector('neuma_splash_symbol', 384, (64, 64, 1))
for qualifier in ('v26', 'v33'):
    monochrome = '\n    <monochrome android:drawable="@drawable/neuma_launcher_monochrome" />' if qualifier == 'v33' else ''
    write(f'mipmap-anydpi-{qualifier}/ic_launcher.xml', f'''<adaptive-icon xmlns:android="http://schemas.android.com/apk/res/android">
    <background android:drawable="@color/neuma_forest" />
    <foreground android:drawable="@drawable/neuma_launcher_foreground" />{monochrome}
</adaptive-icon>''')
shutil.copyfile(SOURCE / 'neuma-logo-horizontal.png', RES / 'drawable-nodpi/neuma_logo_horizontal.png')
# Keep the exploratory HTML self-contained, including when opened with a double click.
preview = ROOT / 'preview/neuma-concept.html'
if preview.exists():
    html = preview.read_text(encoding='utf-8')
    logo = (SOURCE / 'neuma-logo-horizontal.svg').read_text(encoding='utf-8').strip()
    # Presentation crop only: removes the source canvas margins, preserving all artwork.
    logo = logo.replace('viewBox="0 0 1600 480"', 'viewBox="60 100 1100 280" aria-hidden="true"')
    html = re.sub(r'(<button class="wordmark"[^>]*>).*?(</button>)', lambda m: m[1] + logo + m[2], html, count=1)
    html = html.replace('.wordmark svg{width:112px;height:44px;overflow:visible}',
                        '.wordmark svg{width:152px;height:44px;overflow:hidden}')
    favicon = (SOURCE / 'neuma-favicon.svg').read_text(encoding='utf-8').strip()
    html = re.sub(r'<link rel="icon"[^>]*>', '<link rel="icon" href="data:image/svg+xml,' + quote(favicon, safe='') + '">', html, count=1)
    preview.write_text(html, encoding='utf-8')
print('Neuma: marca Android y HTML actualizada desde originales y tokens.')
