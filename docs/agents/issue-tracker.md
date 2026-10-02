# Issue tracker: GitHub

Las tareas y especificaciones viven en GitHub Issues de SaladinoJC/Tesis.
Usar `gh` desde la raíz del repositorio; obtiene el destino de `origin`.

- Crear: `gh issue create --title "..." --body-file <archivo>`
- Leer: `gh issue view <número> --comments`
- Listar: `gh issue list --state open`
- Comentar: `gh issue comment <número> --body-file <archivo>`
- Etiquetar: `gh issue edit <número> --add-label "<etiqueta>"`
- Quitar etiqueta: `gh issue edit <número> --remove-label "<etiqueta>"`
- Cerrar: `gh issue close <número> --comment "..."`

Para textos multilínea, usar archivos con saltos de línea reales.
“Publicar en el issue tracker” significa crear una GitHub Issue.
“Consultar el ticket” significa leer la issue y sus comentarios.

PRs as a request surface: no.
