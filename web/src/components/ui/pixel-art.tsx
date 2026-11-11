/**
 * 도트 그림을 그린다.
 *
 * 그리드 문자열 한 장이 그림 하나다. 색을 바꾸거나 픽셀을 고칠 때
 * 이미지 편집기를 열 필요 없이 문자만 바꾸면 된다.
 * PNG 가 아니라 SVG 라 어느 크기로 키워도 계단이 그대로 유지된다.
 */
export type PixelGrid = readonly string[];

type PixelArtProps = {
  grid: PixelGrid;
  /** 문자 → 색. 여기 없는 문자는 투명하게 둔다. */
  colors: Record<string, string>;
  size?: number;
  className?: string;
  title?: string;
};

export function PixelArt({ grid, colors, size = 64, className, title }: PixelArtProps) {
  const height = grid.length;
  const width = grid[0]?.length ?? 0;

  return (
    <svg
      width={size}
      height={(size * height) / width}
      viewBox={`0 0 ${width} ${height}`}
      shapeRendering="crispEdges"
      className={className}
      role={title ? 'img' : 'presentation'}
      aria-label={title}
    >
      {grid.flatMap((row, y) =>
        [...row].map((char, x) => {
          const fill = colors[char];
          return fill ? <rect key={`${x}-${y}`} x={x} y={y} width={1} height={1} fill={fill} /> : null;
        }),
      )}
    </svg>
  );
}
