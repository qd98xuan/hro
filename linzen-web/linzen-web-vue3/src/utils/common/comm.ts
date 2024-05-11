/**
 * 静态图片资源处理
 * @param path {String} 路径
 */
export const getIotImage = (path: string) => {
  return new URL('/src/assets/images' + path, import.meta.url).href
}