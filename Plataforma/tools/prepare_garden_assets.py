"""Copy approved menu exports/fonts and generate Kotlin tokens. No image editing or downloads."""
from pathlib import Path
import hashlib
import json
import shutil

root = Path(__file__).resolve().parents[2]
res = root / 'Plataforma/app/src/main/res'
drawables = res / 'drawable-nodpi'
drawables.mkdir(parents=True, exist_ok=True)
old = root / 'assets/exports/prototype-menu-simple-v3'
new = root / 'assets/exports/android-menu-v1'
sources = {name: old / f'{name}.webp' for name in ['garden', 'book', 'controls']}
sources['simon'] = old / 'piano-c.webp'
sources.update({name: new / f'{name}.webp' for name in ['instruments', 'rhythm', 'defender', 'character']})
copied = []
for name, source in sources.items():
    target = drawables / f'garden_{name}.webp'
    shutil.copyfile(source, target)
    copied.append({'source': str(source.relative_to(root)), 'target': str(target.relative_to(root)), 'sha256': hashlib.sha256(target.read_bytes()).hexdigest(), 'bytes': target.stat().st_size})
fonts = res / 'font'
fonts.mkdir(exist_ok=True)
licenses = root / 'Plataforma/app/src/main/assets/music-garden/licenses'
licenses.mkdir(parents=True, exist_ok=True)
for name in ['fraunces', 'nunito-sans']:
    source = root / f'design/specimens/fonts/{name}-variable.ttf'
    shutil.copyfile(source, fonts / f'garden_{name.replace("-", "_")}.ttf')
    shutil.copyfile(root / f'design/specimens/fonts/{name}-OFL.txt', licenses / f'{name}-OFL.txt')
tokens = json.loads((root / 'design-tokens.json').read_text(encoding='utf-8'))
lines = ['// Generated from design-tokens.json by tools/prepare_garden_assets.py', 'package ar.edu.unmdp.entrenamiento.ui', '', 'import androidx.compose.ui.graphics.Color', 'import androidx.compose.ui.unit.dp', '', 'internal object GardenTokens {']
for key, value in tokens['color'].items():
    lines.append(f'    val {key[0].upper() + key[1:]} = Color(0xFF{value[1:]})')
for key, value in tokens['motion'].items():
    if isinstance(value, int):
        lines.append(f'    const val {key[0].upper() + key[1:]} = {value}')
for key, value in tokens['spacing'].items():
    lines.append(f'    val Space{key[0].upper() + key[1:]} = {value}.dp')
lines += [f'    val TouchMinimum = {tokens["touch"]["preferredMinimumLogicalSize"]}.dp', f'    val RadiusLarge = {tokens["radius"]["large"]}.dp', '}']
(root / 'Plataforma/app/src/main/java/ar/edu/unmdp/entrenamiento/ui/GardenTokens.kt').write_text('\n'.join(lines) + '\n', encoding='utf-8')
(root / 'assets/exports/android-menu-v1/android-manifest.json').write_text(json.dumps(copied, indent=2), encoding='utf-8')
print(f'{len(copied)} WebP resources copied; fonts/OFL and Kotlin tokens prepared.')
