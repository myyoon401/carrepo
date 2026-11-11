import { PixelArt, type PixelGrid } from '@/components/ui/pixel-art';

/**
 * 커리포 마크.
 *
 * 서류가 세 장 어긋나게 쌓여 삐져나온 상자. "쌓일수록 나를 더 잘 아는" 을 그대로 그렸다 —
 * 종이 더미가 쌓임을, 얼굴이 "나를 안다" 를 맡는다.
 * 종이를 좌우로 어긋나게 둔 이유: 가운데 정렬로 쌓으면 상자 뚜껑처럼 보인다.
 *
 * ⚠️ public/favicon.svg 에 같은 그림이 손으로 한 번 더 그려져 있다.
 *    파비콘은 빌드 전에 정적 파일로 있어야 해서 이 컴포넌트를 쓸 수 없다. 고칠 땐 둘 다 고친다.
 */
const MARK: PixelGrid = [
  '................',
  '.Y......KKKKKK..',
  'YYY.....KWWWWK..',
  '.Y...KKKKKKKK...',
  '.....KWWWWWWK...',
  '..KKKKKKKKKK....',
  '..KWWWWWWWWK....',
  '.KKKKKKKKKKKKKK.',
  '.KBBBBBBBBBBBBK.',
  '.KBBKKBBBBKKBBK.',
  '.KBBKKBBBBKKBBK.',
  '.KBBBBBBBBBBBBK.',
  '.KBBKBBBBBBKBBK.',
  '.KBBBKKKKKKBBBK.',
  '.KBBBBBBBBBBBBK.',
  '.KKKKKKKKKKKKKK.',
];

const COLORS = {
  K: '#111111', // 외곽선
  W: '#ffffff', // 서류
  B: '#5b6fe0', // 상자
  Y: '#ffd43b', // 반짝이
};

export function BrandMark({ size = 56, className }: { size?: number; className?: string }) {
  return <PixelArt grid={MARK} colors={COLORS} size={size} className={className} title="커리포" />;
}
